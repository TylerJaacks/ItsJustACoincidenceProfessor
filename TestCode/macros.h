#ifndef MACROS_H
# define MACROS_H

# ifdef __cplusplus
extern "C" {
# endif

# include <stdio.h>
# include <string.h>
# include <stdlib.h>
# include <errno.h>

/* General purpose macros */

# ifdef VERBOSE_DEBUG
#  define dprintf(...) printf(__VA_ARGS__)
#  define dfprintf(file, ...) fprintf(file, __VA_ARGS__)
# else
#  define dprintf(...)
#  define dfprintf(file, ...)
# endif

# define fieldwidth(i) ((i <= -1000000000)                    ? 11 : \
                        ((i >=  1000000000 || i <= -100000000) ? 10 : \
                         ((i >=   100000000 || i <=  -10000000) ?  9 : \
                          ((i >=    10000000 || i <=   -1000000) ?  8 : \
                           ((i >=     1000000 || i <=    -100000) ?  7 : \
                            ((i >=      100000 || i <=     -10000) ?  6 : \
                             ((i >=       10000 || i <=      -1000) ?  5 : \
                              ((i >=        1000 || i <=       -100) ?  4 : \
                               ((i >=         100 || i <=        -10) ?  3 : \
                                ((i >=          10 || i <=         -1) ?  2 : \
                                 1))))))))))

# ifndef NIAGARA

# define exit(value) {                                                       \
  fprintf(stderr, "exit(" #value ") called at " __FILE__ ":%u\n", __LINE__); \
  exit(value);                                                               \
}

# define fopen(path, mode) ({                                          \
  FILE *_f = fopen(path, mode);                                        \
  if (!_f)                                                             \
    fprintf(stderr, "Failed to open %s: %s\n", path, strerror(errno)); \
  _f;                                                                  \
})

/* If we turn on optimizations, then there are conflicts between a macro *
 * version of strdup() and the macro below.  To avoid that, add the      *
 * conditional block.  Further, if we're optimizing, let's assume that   *
 * don't want all these safe memory macros, so we'll put them in the     *
 * conditional block, too!                                               */
# if !defined __OPTIMIZE__

/*
#  define dmalloc(ptr) ({                                                     \
   double t;                                                                  \
   struct timeval tp;                                                         \
   struct timezone tzp;                                                       \
   gettimeofday(&tp, &tzp);                                                   \
   t = (tzp.tz_minuteswest * 60 + tp.tv_sec) * 1.0e6 + (tp.tv_usec) * 1.0;    \
   printf("%p: %.0f %s " __FILE__ ":%u m\n", ptr, t, __FUNCTION__, __LINE__); \
})

#  define dfree(ptr) ({                                                       \
   double t;                                                                  \
   struct timeval tp;                                                         \
   struct timezone tzp;                                                       \
   gettimeofday(&tp, &tzp);                                                   \
   t = (tzp.tz_minuteswest * 60 + tp.tv_sec) * 1.0e6 + (tp.tv_usec) * 1.0;    \
   printf("%p: %.0f %s " __FILE__ ":%u f\n", ptr, t, __FUNCTION__, __LINE__); \
})
*/


#  define malloc(size) ({                                                  \
   void *_tmp;                                                             \
/*                                                                         \
   printf("Attempting to allocate %d bytes " __FILE__ "%d\n",              \
          size, __LINE__);                                                 \
*/                                                                         \
   _tmp = malloc(size); /* Can't use calloc directly, because it */        \
   if (!_tmp) {               /* invokes the calloc macro.  Use bzero  */  \
     fprintf(stderr, "Allocation failed at %s:%d!\n", __FILE__, __LINE__); \
     exit(1);                                                              \
   }                                                                       \
/*   dmalloc(_tmp);*/                                                      \
   bzero(_tmp, size);                                                      \
   _tmp;                                                                   \
})

#  define calloc(nmemb, size) ({                                           \
   void *_tmp;                                                             \
/*                                                                         \
   printf("Attempting to allocate %d bytes " __FILE__ "%d\n",              \
         (int) (size * nmemb), __LINE__);                                  \
*/                                                                         \
   _tmp = calloc(nmemb, size);                                             \
   if (!_tmp) {                                                            \
     fprintf(stderr, "Allocation failed at %s:%d!\n", __FILE__, __LINE__); \
     exit(1);                                                              \
   }                                                                       \
/*   dmalloc(_tmp);*/                                                      \
   _tmp;                                                                   \
})

#  define strdup(s) ({                                                     \
   char *_s = strdup(s);                                                   \
   if (!_s) {                                                              \
     fprintf(stderr, "Allocation failed at %s:%d!\n", __FILE__, __LINE__); \
     exit(1);                                                              \
   }                                                                       \
/*     dmalloc(_s);*/                                                      \
   _s;                                                                     \
})

#  define free(ptr) ({                              \
   typeof (ptr) _p = (ptr);                         \
/*   dfree(_p);*/                                   \
   free(_p);                                        \
})

# endif /* __OPTIMIZE__ */

# define swap(a, b) ({   \
  typeof (a) _tmp = (a); \
  (a) = (b);             \
  (b) = _tmp;            \
})

# define memswap(a, b) ({    \
  typeof (*(a)) _tmp = *(a); \
  *(a) = *(b);               \
  *(b) = _tmp;               \
})

# define structdup(s) ({                                                  \
  typeof (s) _tmp;                                                        \
  _tmp = malloc(sizeof (*(s)));                                           \
  memcpy(_tmp, (s), sizeof (*(s)));                                       \
  _tmp;                                                                   \
})

# define datacmp(d1, d2) ({                                      \
  register char *_t1 = (char *) d1, *_t2 = (char *) d2, *_start; \
  for (_start = _t1;                                             \
       _t1 < (_start + sizeof (*(d1))) && *_t1 == *_t2;          \
       _t1++, _t2++)                                             \
    ;                                                            \
  _t1 == (_start + sizeof (*(d1)));                              \
})

# define max2(a, b)             \
         ({                     \
	   typeof (a) _a = (a); \
           typeof (b) _b = (b); \
           (_a > _b) ? _a : _b; \
         })

# define min2(a, b)             \
         ({                     \
	   typeof (a) _a = (a); \
           typeof (b) _b = (b); \
           (_a < _b) ? _a : _b; \
         })

# define max max2
# define min min2

# define max3(a, b, c) max(a, max(b, c))
# define min3(a, b, c) min(a, min(b, c))

# define frand() (((double) rand()) / ((double) RAND_MAX))

# ifdef __cplusplus
}
# endif
# else /* NIAGARA */

# define structdup(s) \
  memcpy(malloc(sizeof (*(s))), (s), sizeof (*(s)))

# endif /* NIAGARA */

#endif
