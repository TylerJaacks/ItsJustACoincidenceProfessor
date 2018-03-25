#ifndef NPC_H
# define NPC_H

# include <stdint.h>

# include "dims.h"

# define NPC_SMART         0x00000001
# define NPC_TELEPATH      0x00000002
# define NPC_TUNNEL        0x00000004
# define NPC_ERRATIC       0x00000008
# define NPC_BIT04         0x00000010
# define NPC_BIT05         0x00000020
# define NPC_BIT06         0x00000040
# define NPC_BIT07         0x00000080
# define NPC_BIT08         0x00000100
# define NPC_BIT09         0x00000200
# define NPC_BIT10         0x00000400
# define NPC_BIT11         0x00000800
# define NPC_BIT12         0x00001000
# define NPC_BIT13         0x00002000
# define NPC_BIT14         0x00004000
# define NPC_BIT15         0x00008000
# define NPC_BIT16         0x00010000
# define NPC_BIT17         0x00020000
# define NPC_BIT18         0x00040000
# define NPC_BIT19         0x00080000
# define NPC_BIT20         0x00100000
# define NPC_BIT21         0x00200000
# define NPC_BIT22         0x00400000
# define NPC_BIT23         0x00800000
# define NPC_BIT24         0x01000000
# define NPC_BIT25         0x02000000
# define NPC_BIT26         0x04000000
# define NPC_BIT27         0x08000000
# define NPC_BIT28         0x10000000
# define NPC_BIT29         0x20000000
# define NPC_BIT30         0x40000000
# define NPC_BIT31         0x80000000

# define has_characteristic(character, bit)              \
  ((character)->npc->characteristics & NPC_##bit)

typedef struct dungeon dungeon_t;
typedef struct character character_t;
typedef uint32_t npc_characteristics_t;

typedef struct npc {
  npc_characteristics_t characteristics;
  uint32_t have_seen_pc;
  pair_t pc_last_known_position;

} npc_t;

void gen_monsters(dungeon_t *d);
void npc_delete(npc_t *n);
void npc_next_pos(dungeon_t *d, character_t *c, pair_t next);
uint32_t dungeon_has_npcs(dungeon_t *d);

#endif
