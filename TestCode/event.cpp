#include "event.h"
#include "character.h"

static uint32_t next_event_number(void)
{
  static uint32_t sequence_number;

  /* We need to special case the first PC insert, because monsters go *
   * into the queue before the PC.  Pre-increment ensures that this   *
   * starts at 1, so we can use a zero there.                         */
  return ++sequence_number;
}

int32_t compare_events(const void *event1, const void *event2)
{
  int32_t difference;

  difference = (((event_t *) event1)->time -
                ((event_t *) event2)->time);
  return difference ? difference : (((event_t *) event1)->sequence -
                                    ((event_t *) event2)->sequence);

}

event_t *new_event(dungeon_t *d, event_type_t t, void *v, uint32_t delay)
{
  event_t *e;

  e = (event_t*) malloc(sizeof (*e));

  e->type = t;
  e->time = d->time + delay;
  e->sequence = next_event_number();
  switch (t) {
  case event_character_turn:
    e->c = (character_t *)v;
  }

  return e;
}

event_t *update_event(dungeon_t *d, event_t *e, uint32_t delay)
{
  e->time = d->time + delay;
  e->sequence = next_event_number();

  return e;
}

void event_delete(void *e)
{
  event_t *event = (event_t *)e;

  switch (event->type) {
  case event_character_turn:
    character_delete(event->c);
    break;
  }

  free(event);
}
