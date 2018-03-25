#include "heap.h"
#include "macros.h"

#undef min

struct heap_node {
  heap_node_t *next;
  heap_node_t *prev;
  heap_node_t *parent;
  heap_node_t *child;
  void *datum;
  uint32_t degree;
  uint32_t mark;
};

#define splice_heap_node_lists(n1, n2) ({ \
  if ((n1) && (n2)) {                     \
    (n1)->next->prev = (n2)->prev;        \
    (n2)->prev->next = (n1)->next;        \
    (n1)->next = (n2);                    \
    (n2)->prev = (n1);                    \
  }                                       \
})

#define insert_heap_node_in_list(n, l) ({ \
  (n)->next = (l);                        \
  (n)->prev = (l)->prev;                  \
  (n)->prev->next = (n);                  \
  (l)->prev = (n);                        \
})

#define remove_heap_node_from_list(n) ({ \
  (n)->next->prev = (n)->prev;           \
  (n)->prev->next = (n)->next;           \
})

void print_heap_node(heap_node_t *n, unsigned indent,
                     char *(*print)(const void *v))
{
  heap_node_t *nc;

  printf("%*s%s\n", indent, "", print(n->datum));
  if (!(nc = n->child)) {
    return;
  }

  do {
    print_heap_node(nc, indent + 2, print);
    nc = nc->next;
  } while (nc != n->child);
}

void print_heap(heap_t *h, char *(*print)(const void *v))
{
  heap_node_t *n;

  if (h->min) {
    printf("size = %u\n", h->size);
    printf("min = ");
    n = h->min;
    do {
      print_heap_node(n, 0, print);
      n = n->next;
    } while (n != h->min);
  } else {
    printf("(null)\n");
  }
}

void print_heap_node_list(heap_node_t *n)
{
  heap_node_t *hn;

  if (!n) {
    return;
  }

  hn = n;
  do {
    printf("%p ", hn->datum);
    hn = hn->next;
  } while (hn != n);
  printf("\n");
}

void heap_init(heap_t *h,
               int32_t (*compare)(const void *key, const void *with),
               void (*datum_delete)(void *))
{
  h->min = NULL;
  h->size = 0;
  h->compare = compare;
  h->datum_delete = datum_delete;
}

void heap_node_delete(heap_t *h, heap_node_t *hn)
{
  heap_node_t *next;

  hn->prev->next = NULL;
  while (hn) {
    if (hn->child) {
      heap_node_delete(h, hn->child);
    } 
    next = hn->next;
    if (h->datum_delete) {
      h->datum_delete(hn->datum);
    }
    free(hn);
    hn = next;
  }
}

void heap_delete(heap_t *h)
{
  if (h->min) {
    heap_node_delete(h, h->min);
  }
  h->min = NULL;
  h->size = 0;
  h->compare = NULL;
  h->datum_delete = NULL;
}

heap_node_t *heap_insert(heap_t *h, void *v)
{
  heap_node_t *n;

  n = (heap_node_t*)calloc(1, sizeof (*n));
  n->datum = v;

  if (h->min) {
    insert_heap_node_in_list(n, h->min);
  } else {
    n->next = n->prev = n;
  }
  if (!h->min || (h->compare(v, h->min->datum) < 0)) {
    h->min = n;
  }
  h->size++;

  return n;
}

void *heap_peek_min(heap_t *h)
{
  return h->min ? h->min->datum : NULL;
}

static void heap_link(heap_t *h, heap_node_t *node, heap_node_t *root)
{
  /*  remove_heap_node_from_list(node);*/
  if (root->child) {
    insert_heap_node_in_list(node, root->child);
  } else {
    root->child = node;
    node->next = node->prev = node;
  }
  node->parent = root;
  root->degree++;
  node->mark = 0;
}

static void heap_consolidate(heap_t *h)
{
  uint32_t i;
  heap_node_t *x, *y, *n;
  heap_node_t *a[64]; /* Need ceil(lg(h->size)), so this is good  *
                       * to the limit of a 64-bit address space,  *
                       * and much faster than any lg calculation. */

  memset(a, 0, sizeof (a));

  h->min->prev->next = NULL;

  for (x = n = h->min; n; x = n) {
    n = n->next;

    while (a[x->degree]) {
      y = a[x->degree];
      if (h->compare(x->datum, y->datum) > 0) {
        swap(x, y);
      }
      a[x->degree] = NULL;
      heap_link(h, y, x);
    }
    a[x->degree] = x;
  }

  for (h->min = NULL, i = 0; i < 64; i++) {
    if (a[i]) {
      if (h->min) {
        insert_heap_node_in_list(a[i], h->min);
        if (h->compare(a[i]->datum, h->min->datum) < 0) {
          h->min = a[i];
        }
      } else {
        h->min = a[i];
        a[i]->next = a[i]->prev = a[i];
      }
    }
  }
}

void *heap_remove_min(heap_t *h)
{
  void *v;
  heap_node_t *n;

  v = NULL;

  if (h->min) {
    v = h->min->datum;
    if (h->size == 1) {
      free(h->min);
      h->min = NULL;
    } else {
      if ((n = h->min->child)) {
        for (; n->parent; n = n->next) {
          n->parent = NULL;
        }
      }

      splice_heap_node_lists(h->min, h->min->child);

      n = h->min;
      remove_heap_node_from_list(n);
      h->min = n->next;
      free(n);

      heap_consolidate(h);
    }

    h->size--;
  }

  return v;
}

int heap_combine(heap_t *h, heap_t *h1, heap_t *h2)
{
  if (h1->compare != h2->compare ||
      h1->datum_delete != h2->datum_delete) {
    return 1;
  }

  h->compare = h1->compare;
  h->datum_delete = h1->datum_delete;

  if (!h1->min) {
    h->min = h2->min;
    h->size = h2->size;
  } else if (!h2->min) {
    h->min = h1->min;
    h->size = h1->size;
  } else {
    h->min = ((h->compare(h1->min->datum, h2->min->datum) < 0) ?
              h1->min                                          :
              h2->min);
    splice_heap_node_lists(h1->min, h2->min);
  }

  memset(h1, 0, sizeof (*h1));
  memset(h2, 0, sizeof (*h2));

  return 0;
}

static void heap_cut(heap_t *h, heap_node_t *n, heap_node_t *p)
{
  if (!--p->degree) {
    p->child = NULL;
  }
  if (p->child == n) {
    p->child = p->child->next;
  }
  remove_heap_node_from_list(n);
  n->parent = NULL;
  n->mark = 0;
  insert_heap_node_in_list(n, h->min);
}

static void heap_cascading_cut(heap_t *h, heap_node_t *n)
{
  heap_node_t *p;

  if ((p = n->parent)) {
    if (!n->mark) {
      n->mark = 1;
    } else {
      heap_cut(h, n, p);
      heap_cascading_cut(h, n);
    }
  }
}

int heap_decrease_key(heap_t *h, heap_node_t *n, void *v)
{
  if (h->compare(n->datum, v) <= 0) {
    return 1;
  }

  if (h->datum_delete) {
    h->datum_delete(n->datum);
  }
  n->datum = v;

  return heap_decrease_key_no_replace(h, n);
}

int heap_decrease_key_no_replace(heap_t *h, heap_node_t *n)
{
  /* No tests that the value hasn't actually increased.  Change *
   * occurs in place, so the check is not possible here.  The   *
   * user is completely responsible for ensuring that they      *
   * don't fubar the queue.                                     */

  heap_node_t *p;

  p = n->parent;

  if (p && (h->compare(n->datum, p->datum) < 0)) {
    heap_cut(h, n, p);
    heap_cascading_cut(h, p);
  }
  if (h->compare(n->datum, h->min->datum) < 0) {
    h->min = n;
  }

  return 0;
}

#ifdef TESTING

int32_t compare(const void *key, const void *with)
{
  return *((int *) key) - *((int *) with);
}

char *print_int(const void *v)
{
  static char out[640];

  snprintf(out, 640, "%d", *((int *) v));

  return out;
}

int main(int argc, char *argv[])
{
  heap_t h;
  int **keys;
  heap_node_t **a;
  /*  int *p;*/
  int i, j;
  int n;

  if (argc == 2) {
    n = atoi(argv[1]);
  } else {
    n = 20;
  }

  keys = (int*)calloc(n, sizeof (*keys));
  a = (heap_node_t *)calloc(n, sizeof (*a));

  heap_init(&h, compare, free);

  for (i = 0; i < n; i++) {
    keys[i] = (int *)malloc(sizeof (*keys[i])); //ATTENTION error here?
    *keys[i] = i;
    a[i] = heap_insert(&h, keys[i]);
  }

  print_heap(&h, print_int);
  printf("------------------------------------\n");
  
  heap_remove_min(&h);
  keys[0] = (int *)malloc(sizeof (*keys[0]));
  *keys[0] = 0;
  a[0] = heap_insert(&h, keys[0]);
  for (i = 0; i < 100 * n; i++) {
    j = rand() % n;
    /*    p = malloc (sizeof (*p));*/
    (*(int *) a[j]->datum)--;
    /*    (*p)--;*/
    heap_decrease_key_no_replace(&h, a[j]);
    print_heap(&h, print_int);
    printf("------------------------------------\n");
  }

  free(keys);

  return 0;
}

#endif
