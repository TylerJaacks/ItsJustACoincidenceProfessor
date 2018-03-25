#ifndef HEAP_H
# define HEAP_H

# ifdef __cplusplus
extern "C" {
# endif

# include <stdint.h>

struct heap_node;
typedef struct heap_node heap_node_t;

typedef struct heap {
  heap_node_t *min;
  uint32_t size;
  int32_t (*compare)(const void *key, const void *with);
  void (*datum_delete)(void *);
} heap_t;

void heap_init(heap_t *h,
               int32_t (*compare)(const void *key, const void *with),
               void (*datum_delete)(void *));
void heap_delete(heap_t *h);
heap_node_t *heap_insert(heap_t *h, void *v);
void *heap_peek_min(heap_t *h);
void *heap_remove_min(heap_t *h);
int heap_combine(heap_t *h, heap_t *h1, heap_t *h2);
int heap_decrease_key(heap_t *h, heap_node_t *n, void *v);
int heap_decrease_key_no_replace(heap_t *h, heap_node_t *n);

# ifdef __cplusplus
}
# endif

#endif
