#include "path.h"
#include "dungeon.h"

/* Ugly hack: There is no way to pass a pointer to the dungeon into the *
 * heap's comparitor funtion without modifying the heap.  Copying the   *
 * pc_distance array is a possible solution, but that doubles the       *
 * bandwidth requirements for dijkstra, which would also be bad.        *
 * Instead, make a global pointer to the dungeon in this file,          *
 * initialize it in dijkstra, and use it in the comparitor to get to    *
 * pc_distance.  Otherwise, pretend it doesn't exist, because it really *
 * is ugly.                                                             */
static dungeon_t *dungeon;

typedef struct path {
  heap_node_t *hn;
  uint8_t pos[2];
} path_t;

static int32_t dist_cmp(const void *key, const void *with) {
  return ((int32_t) dungeon->pc_distance[((path_t *) key)->pos[dim_y]]
                                        [((path_t *) key)->pos[dim_x]] -
          (int32_t) dungeon->pc_distance[((path_t *) with)->pos[dim_y]]
                                        [((path_t *) with)->pos[dim_x]]);
}

static int32_t tunnel_cmp(const void *key, const void *with) {
  return ((int32_t) dungeon->pc_tunnel[((path_t *) key)->pos[dim_y]]
                                      [((path_t *) key)->pos[dim_x]] -
          (int32_t) dungeon->pc_tunnel[((path_t *) with)->pos[dim_y]]
                                      [((path_t *) with)->pos[dim_x]]);
}

void dijkstra(dungeon_t *d)
{
  /* Currently assumes that monsters only move on floors.  Will *
   * need to be modified for tunneling and pass-wall monsters.  */

  heap_t h;
  uint32_t x, y;
  static path_t p[DUNGEON_Y][DUNGEON_X], *c;
  static uint32_t initialized = 0;

  if (!initialized) {
    initialized = 1;
    dungeon = d;
    for (y = 0; y < DUNGEON_Y; y++) {
      for (x = 0; x < DUNGEON_X; x++) {
        p[y][x].pos[dim_y] = y;
        p[y][x].pos[dim_x] = x;
      }
    }
  }

  for (y = 0; y < DUNGEON_Y; y++) {
    for (x = 0; x < DUNGEON_X; x++) {
      d->pc_distance[y][x] = 255;
    }
  }
  d->pc_distance[d->pc.position[dim_y]][d->pc.position[dim_x]] = 0;

  heap_init(&h, dist_cmp, NULL);

  for (y = 0; y < DUNGEON_Y; y++) {
    for (x = 0; x < DUNGEON_X; x++) {
      if (mapxy(x, y) >= ter_floor) {
        p[y][x].hn = heap_insert(&h, &p[y][x]);
      }
    }
  }

  while ((c = (path_t *)heap_remove_min(&h))) {
    c->hn = NULL;
    if ((p[c->pos[dim_y] - 1][c->pos[dim_x] - 1].hn) &&
        (d->pc_distance[c->pos[dim_y] - 1][c->pos[dim_x] - 1] >
         d->pc_distance[c->pos[dim_y]][c->pos[dim_x]] + 1)) {
      d->pc_distance[c->pos[dim_y] - 1][c->pos[dim_x] - 1] =
        d->pc_distance[c->pos[dim_y]][c->pos[dim_x]] + 1;
      heap_decrease_key_no_replace(&h,
                                   p[c->pos[dim_y] - 1][c->pos[dim_x] - 1].hn);
    }
    if ((p[c->pos[dim_y] - 1][c->pos[dim_x]    ].hn) &&
        (d->pc_distance[c->pos[dim_y] - 1][c->pos[dim_x]    ] >
         d->pc_distance[c->pos[dim_y]][c->pos[dim_x]] + 1)) {
      d->pc_distance[c->pos[dim_y] - 1][c->pos[dim_x]    ] =
        d->pc_distance[c->pos[dim_y]][c->pos[dim_x]] + 1;
      heap_decrease_key_no_replace(&h,
                                   p[c->pos[dim_y] - 1][c->pos[dim_x]    ].hn);
    }
    if ((p[c->pos[dim_y] - 1][c->pos[dim_x] + 1].hn) &&
        (d->pc_distance[c->pos[dim_y] - 1][c->pos[dim_x] + 1] >
         d->pc_distance[c->pos[dim_y]][c->pos[dim_x]] + 1)) {
      d->pc_distance[c->pos[dim_y] - 1][c->pos[dim_x] + 1] =
        d->pc_distance[c->pos[dim_y]][c->pos[dim_x]] + 1;
      heap_decrease_key_no_replace(&h,
                                   p[c->pos[dim_y] - 1][c->pos[dim_x] + 1].hn);
    }
    if ((p[c->pos[dim_y]    ][c->pos[dim_x] - 1].hn) &&
        (d->pc_distance[c->pos[dim_y]    ][c->pos[dim_x] - 1] >
         d->pc_distance[c->pos[dim_y]][c->pos[dim_x]] + 1)) {
      d->pc_distance[c->pos[dim_y]    ][c->pos[dim_x] - 1] =
        d->pc_distance[c->pos[dim_y]][c->pos[dim_x]] + 1;
      heap_decrease_key_no_replace(&h,
                                   p[c->pos[dim_y]    ][c->pos[dim_x] - 1].hn);
    }
    if ((p[c->pos[dim_y]    ][c->pos[dim_x] + 1].hn) &&
        (d->pc_distance[c->pos[dim_y]    ][c->pos[dim_x] + 1] >
         d->pc_distance[c->pos[dim_y]][c->pos[dim_x]] + 1)) {
      d->pc_distance[c->pos[dim_y]    ][c->pos[dim_x] + 1] =
        d->pc_distance[c->pos[dim_y]][c->pos[dim_x]] + 1;
      heap_decrease_key_no_replace(&h,
                                   p[c->pos[dim_y]    ][c->pos[dim_x] + 1].hn);
    }
    if ((p[c->pos[dim_y] + 1][c->pos[dim_x] - 1].hn) &&
        (d->pc_distance[c->pos[dim_y] + 1][c->pos[dim_x] - 1] >
         d->pc_distance[c->pos[dim_y]][c->pos[dim_x]] + 1)) {
      d->pc_distance[c->pos[dim_y] + 1][c->pos[dim_x] - 1] =
        d->pc_distance[c->pos[dim_y]][c->pos[dim_x]] + 1;
      heap_decrease_key_no_replace(&h,
                                   p[c->pos[dim_y] + 1][c->pos[dim_x] - 1].hn);
    }
    if ((p[c->pos[dim_y] + 1][c->pos[dim_x]    ].hn) &&
        (d->pc_distance[c->pos[dim_y] + 1][c->pos[dim_x]    ] >
         d->pc_distance[c->pos[dim_y]][c->pos[dim_x]] + 1)) {
      d->pc_distance[c->pos[dim_y] + 1][c->pos[dim_x]    ] =
        d->pc_distance[c->pos[dim_y]][c->pos[dim_x]] + 1;
      heap_decrease_key_no_replace(&h,
                                   p[c->pos[dim_y] + 1][c->pos[dim_x]    ].hn);
    }
    if ((p[c->pos[dim_y] + 1][c->pos[dim_x] + 1].hn) &&
        (d->pc_distance[c->pos[dim_y] + 1][c->pos[dim_x] + 1] >
         d->pc_distance[c->pos[dim_y]][c->pos[dim_x]] + 1)) {
      d->pc_distance[c->pos[dim_y] + 1][c->pos[dim_x] + 1] =
        d->pc_distance[c->pos[dim_y]][c->pos[dim_x]] + 1;
      heap_decrease_key_no_replace(&h,
                                   p[c->pos[dim_y] + 1][c->pos[dim_x] + 1].hn);
    }
  }
  heap_delete(&h);
}

/* Ignores the case of hardness == 255, because if *
 * that gets here, there's already been an error.  */
#define tunnel_movement_cost(x, y)                      \
  ((d->hardness[y][x] / 85) + 1)

void dijkstra_tunnel(dungeon_t *d)
{
  /* Currently assumes that monsters only move on floors.  Will *
   * need to be modified for tunneling and pass-wall monsters.  */

  heap_t h;
  uint32_t x, y;
  int size;
  static path_t p[DUNGEON_Y][DUNGEON_X], *c;
  static uint32_t initialized = 0;

  if (!initialized) {
    initialized = 1;
    dungeon = d;
    for (y = 0; y < DUNGEON_Y; y++) {
      for (x = 0; x < DUNGEON_X; x++) {
        p[y][x].pos[dim_y] = y;
        p[y][x].pos[dim_x] = x;
      }
    }
  }

  for (y = 0; y < DUNGEON_Y; y++) {
    for (x = 0; x < DUNGEON_X; x++) {
      d->pc_tunnel[y][x] = 255;
    }
  }
  d->pc_tunnel[d->pc.position[dim_y]][d->pc.position[dim_x]] = 0;

  heap_init(&h, tunnel_cmp, NULL);

  for (y = 0; y < DUNGEON_Y; y++) {
    for (x = 0; x < DUNGEON_X; x++) {
      if (mapxy(x, y) != ter_wall_immutable) {
        p[y][x].hn = heap_insert(&h, &p[y][x]);
      }
    }
  }

  size = h.size;
  while ((c = (path_t *)heap_remove_min(&h))) {
    if (--size != (int)h.size) {
      exit(1);
    }
    c->hn = NULL;
    if ((p[c->pos[dim_y] - 1][c->pos[dim_x] - 1].hn) &&
        (d->pc_tunnel[c->pos[dim_y] - 1][c->pos[dim_x] - 1] >
         d->pc_tunnel[c->pos[dim_y]][c->pos[dim_x]] +
         tunnel_movement_cost(c->pos[dim_x], c->pos[dim_y]))) {
      d->pc_tunnel[c->pos[dim_y] - 1][c->pos[dim_x] - 1] =
        (d->pc_tunnel[c->pos[dim_y]][c->pos[dim_x]] +
         tunnel_movement_cost(c->pos[dim_x], c->pos[dim_y]));
      heap_decrease_key_no_replace(&h,
                                   p[c->pos[dim_y] - 1][c->pos[dim_x] - 1].hn);
    }
    if ((p[c->pos[dim_y] - 1][c->pos[dim_x]    ].hn) &&
        (d->pc_tunnel[c->pos[dim_y] - 1][c->pos[dim_x]    ] >
         d->pc_tunnel[c->pos[dim_y]][c->pos[dim_x]] +
         tunnel_movement_cost(c->pos[dim_x], c->pos[dim_y]))) {
      d->pc_tunnel[c->pos[dim_y] - 1][c->pos[dim_x]    ] =
        (d->pc_tunnel[c->pos[dim_y]][c->pos[dim_x]] +
         tunnel_movement_cost(c->pos[dim_x], c->pos[dim_y]));
      heap_decrease_key_no_replace(&h,
                                   p[c->pos[dim_y] - 1][c->pos[dim_x]    ].hn);
    }
    if ((p[c->pos[dim_y] - 1][c->pos[dim_x] + 1].hn) &&
        (d->pc_tunnel[c->pos[dim_y] - 1][c->pos[dim_x] + 1] >
         d->pc_tunnel[c->pos[dim_y]][c->pos[dim_x]] +
         tunnel_movement_cost(c->pos[dim_x], c->pos[dim_y]))) {
      d->pc_tunnel[c->pos[dim_y] - 1][c->pos[dim_x] + 1] =
        (d->pc_tunnel[c->pos[dim_y]][c->pos[dim_x]] +
         tunnel_movement_cost(c->pos[dim_x], c->pos[dim_y]));
      heap_decrease_key_no_replace(&h,
                                   p[c->pos[dim_y] - 1][c->pos[dim_x] + 1].hn);
    }
    if ((p[c->pos[dim_y]    ][c->pos[dim_x] - 1].hn) &&
        (d->pc_tunnel[c->pos[dim_y]    ][c->pos[dim_x] - 1] >
         d->pc_tunnel[c->pos[dim_y]][c->pos[dim_x]] +
         tunnel_movement_cost(c->pos[dim_x], c->pos[dim_y]))) {
      d->pc_tunnel[c->pos[dim_y]    ][c->pos[dim_x] - 1] =
        (d->pc_tunnel[c->pos[dim_y]][c->pos[dim_x]] +
         tunnel_movement_cost(c->pos[dim_x], c->pos[dim_y]));
      heap_decrease_key_no_replace(&h,
                                   p[c->pos[dim_y]    ][c->pos[dim_x] - 1].hn);
    }
    if ((p[c->pos[dim_y]    ][c->pos[dim_x] + 1].hn) &&
        (d->pc_tunnel[c->pos[dim_y]    ][c->pos[dim_x] + 1] >
         d->pc_tunnel[c->pos[dim_y]][c->pos[dim_x]] +
         tunnel_movement_cost(c->pos[dim_x], c->pos[dim_y]))) {
      d->pc_tunnel[c->pos[dim_y]    ][c->pos[dim_x] + 1] =
        (d->pc_tunnel[c->pos[dim_y]][c->pos[dim_x]] +
         tunnel_movement_cost(c->pos[dim_x], c->pos[dim_y]));
      heap_decrease_key_no_replace(&h,
                                   p[c->pos[dim_y]    ][c->pos[dim_x] + 1].hn);
    }
    if ((p[c->pos[dim_y] + 1][c->pos[dim_x] - 1].hn) &&
        (d->pc_tunnel[c->pos[dim_y] + 1][c->pos[dim_x] - 1] >
         d->pc_tunnel[c->pos[dim_y]][c->pos[dim_x]] +
         tunnel_movement_cost(c->pos[dim_x], c->pos[dim_y]))) {
      d->pc_tunnel[c->pos[dim_y] + 1][c->pos[dim_x] - 1] =
        (d->pc_tunnel[c->pos[dim_y]][c->pos[dim_x]] +
         tunnel_movement_cost(c->pos[dim_x], c->pos[dim_y]));
      heap_decrease_key_no_replace(&h,
                                   p[c->pos[dim_y] + 1][c->pos[dim_x] - 1].hn);
    }
    if ((p[c->pos[dim_y] + 1][c->pos[dim_x]    ].hn) &&
        (d->pc_tunnel[c->pos[dim_y] + 1][c->pos[dim_x]    ] >
         d->pc_tunnel[c->pos[dim_y]][c->pos[dim_x]] +
         tunnel_movement_cost(c->pos[dim_x], c->pos[dim_y]))) {
      d->pc_tunnel[c->pos[dim_y] + 1][c->pos[dim_x]    ] =
        (d->pc_tunnel[c->pos[dim_y]][c->pos[dim_x]] +
         tunnel_movement_cost(c->pos[dim_x], c->pos[dim_y]));
      heap_decrease_key_no_replace(&h,
                                   p[c->pos[dim_y] + 1][c->pos[dim_x]    ].hn);
    }
    if ((p[c->pos[dim_y] + 1][c->pos[dim_x] + 1].hn) &&
        (d->pc_tunnel[c->pos[dim_y] + 1][c->pos[dim_x] + 1] >
         d->pc_tunnel[c->pos[dim_y]][c->pos[dim_x]] +
         tunnel_movement_cost(c->pos[dim_x], c->pos[dim_y]))) {
      d->pc_tunnel[c->pos[dim_y] + 1][c->pos[dim_x] + 1] =
        (d->pc_tunnel[c->pos[dim_y]][c->pos[dim_x]] +
         tunnel_movement_cost(c->pos[dim_x], c->pos[dim_y]));
      heap_decrease_key_no_replace(&h,
                                   p[c->pos[dim_y] + 1][c->pos[dim_x] + 1].hn);
    }
  }
  heap_delete(&h);
}
