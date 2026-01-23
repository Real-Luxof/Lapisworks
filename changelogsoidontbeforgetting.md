waddup  
this IS a changelog for me to keep track of stuff but it's also a todo-list, which is why some stuff is so detailed  

Ah hell naw chat you're tweaking I can't do this fucking marathon
Luxof didn't even DREAM of this shit in his prime (1.1.0-1.2.0)
# 1.5.1
- Updated Cubic Exaltation and Spherical Exaltation's out-of-date names
- Updated Cubic Exaltation's arguments to be `[pattern], vec, vec, bool` instead of `[pattern], vec, num, bool`
- Fixed bug where Cubic Exaltation wouldn't clear the arguments after use
- Added the Simple Mind Container (and its scrying lens info overlay)
- Added Thought Sieving
- Added Mind Liquefaction
- Added Cognition Purification
# 1.5.1.5
- fixed bug where Simple Mind Container wouldn't gain Mind (tied to bug below, actually)
- fixed bug where just by existing for long enough you could nullify villager consumption cd
- fixed bug where Mind Liquefaction wouldn't take anything but a full container
# 1.5.2
- Patchouli and hexdoc interop for per world shape patterns
- Refactored like 40% of the code to be less painful to fw lmao
- fixed a capitalization mistake and "Format Error:" in some places of the book
- fixed no translation for the individual variants of Summon Enchanted Sentinel
- fixed weird ass bug with Enchant X body part patterns where they'd try to take negative Amel
  (i dunno if this one was purely in the devving or not)
- fixed enchanted attrs not carrying over across logins
- Added what I forgor to the sword descs
- made Thought Sieve consume 50% of the mind, and not just 25%
- Added Live Jukebox and it's companion pattern "Teach Song"
- Added Imbue Mind with recipes:
    - Amethyst Block -> Budding Amethyst
    - Jukebox -> Live Jukebox
# 1.5.3
- Fixed enchantments being able to take too much Amel and making negative nums
- Fixed Imbue Amel taking too much media
- Fixed Imbue Amel not properly doing it's fucking thing of repairing shit
- Fixed a potential crash in Imbue Amel
- Fixed LivingEntity mixin crashing because i can't mixin to constructors for shit (just removed the inject)
- Made the Patchouli book read better in some places
- Renamed the old Imbue Mind (the one that recharges stuff) to Mind Liquefaction
- Mainhand-reading patterns have been generalized to any hand
- Also generalized many patterns to take any hand
- generalization has allowed most patterns to work on casting circles too
- Mainhand mishap has been generalized to any hand as well
- Not Enough Items In Offhand mishap has been generalized as well
- Added Equivalent Block D.
- Added Equal Block Dist.
- Added Hastenature
- Imbue Amel can now lowkey make enchanted books more powerful
# 1.5.4
- Renamed the "Wrong Item In a Hand" mishap to "Wrong Item In Hand" in the book
- Fixed Imbue Amel using the Incorrect Item mishap for the off-hand where it needs Amel and the
  Wrong Item In Hand mishap for the main-hand where it needs an imbueable item
- Made Simple Mind Container's filling have more frames
  (now a whopping 15 instead of 4, that's almost 4²! /sarcasm)
- Gave Enchanted Book enhancement with Imbue Amel an actual page in the book
- Made Enchanted Book enhancement with Imbue Amel take 20 * previous level Amel
- Added the Jump Slate
  (WHY WAS THAT SO HARD TO MIXIN)
- Added variants of the Jump Slate
  gave em support with Mold Amel too
- Turned the ancient wizard fully gender neutral this time (headcanon the gender yourself)
# 1.5.4.5
Whoopsies
- "pages.lapisworks.imbuement_artmind.reflection2" lmao deleted that
- Fixed Jump Slate stuff appearing before enlightenment
  made it appear after enlightenment and got_lapis in a scuffed ahh way that is hopefully never seen
# 1.5.5
- Fixed up the ingame book a little (stopped implying GSent was visible, etc.)
  Also fixed the bug where it wouldn't load on multiplayer!
- Gave the Warped Infused Staff an actual translation key (bruh how did i forger that)
- Fixed possible bug with Hastenature (wtf)
- Fixed bug with PWShape interop for Patchouli
- Fixed bug where Jump Slate would always jump forwards no matter what
- Gave the web book a custom icon
- Gave Jump Slate a friend: Rebound Slate
- Hextended Gear's staves have Partially Amel-infused variations of them now
  my hands bleed
  31 staves
  - extended wood staves
  - mossy staves
  - prismarine staves
  - obsidian staves
  - purpur staves
  - extended fanciful staves
- fully amel wands have 28% hex grid boost
- partially amel wands have 40% hex grid boost
- Buffed partially amel staves' durability from 100 to 200
- Debuffed fully amel staves' hex grid boost from 25% to 20%
- Debuffed partially amel staves' hex grid boost from 33% to 30%
- Amel staff and incomplete amel staves are now held like the vanilla hexcasting staves
- Changed every hex grid space modifying item to multiply by base, not by total.
- Also added the block counterpart to the Drawing Orb, the Amel-tuned Drawing Orb

**NOTE FOR MIGRATORS:**
per-world shape patterns have once again changed, this will be the last time. i think.
# 1.5.5.5
- Fixed staffcasting not working if you don't have hextended (WTF????)
- Also fixed some staves not opening their spellcasting gui
# 1.5.6
1.1.7: "haha i added some funny swords"  
1.5.6's honest reaction to that information:
- Imbue Amel now costs 2xAmel in *dust*, not in *shards*
- Many patterns that previously took Amel are more convenient now.
- Simple Mind Containers now look good in the offhand too
- Refactored, like, another 40% of the codebase
- Fixed up the book a little
- Fixed a few errors in the book
- Fixed Amel Swords not working for a bit
- Fixed Amel Wand being able to be made with 10 Amel for a bit and shit like that (wtf?)
- Fixed Enchant Skin
- Fixed Imbue Amel and mishap bugs
- Fixed Lapisworks crashing with hexxy4's Hex Casting build
- Fixed Mold Amel saying it needs Amel and not a moldable substance when it doesn't find a moldable substance
- Fixed partamel variants of the Obsidian wand/staff not existing
- Fixed the Incomplete Staff of Amethyst Lazuli never being able to graduate to a complete staff
- Lapisworks can be datapacked for shit now (will add a wiki for how right after this update)
- Added Reclaim Amethyst
- Added the Amel Jar to store 4 stacks of Amel
  - It also renders on you when you equip it in the belt slot
  - Works from your hotbar too
- Added the Enchantment Energy Container to store 16 stacks of Amel
  - Can't be equipped but works from your hotbar like the Amel Jar
- Added interop with Hexical
  - Added the Copper Rod
  - Added the Amel-Copper Item Cradle
  - Added the Handed Prison for v2.0.0

**NOTE FOR MIGRATORS:**
Super sorry, but this is the LAST!! time per-world shape patterns change!
# 1.5.6.5
- Fixed the Copper Rod and Amel-Copper Item Cradle and the Handed Prison not dropping their items
- Fixed those items also not being mineable
# 1.5.6.6
- Fixed up the book a little (online and patchouli)
- Fixed Dark Primarine Staves having no Amel Imbuement recipe
- Fixed crash lmao
# 1.5.6.7
- Technically Amel Imbuement is datapack-friendlier now but untrusted (unfinished i think)
- Fixed BeegInfusions not fucking working a lot of the time
- Fixed no Amel Imbuement recipe for Casting Rings
# 1.5.6.8
- Read 1.5.6.7's changelog. Yeah.
- Fixed partially amel stuff's durability not changing.
# 1.5.6.9
- Fixed requiring Hexical or it'll break the book :sob:
# 1.5.7
I randomly did like 15% of this update in one day, in 5 hours.
Was I fucking LAZY before and after??? (Note from future me: yes.)
## Additions:
  - Empty Distillation
    - has Visible Distillation's previous functionality
  - Focus Necklace
  - Geode Dowser
    - Imbue 5 Amel into a compass
    - Consumes 1 amethyst dust per use
  - Simple Impetus
    - Infuse a Simple Mind into an empty Impetus
    - By default executes when ANY pattern is executed nearby
    - Can be taught to only execute on specific patterns
  - Media Condensing Unit
    - Deposit with Deposit Media (10% dust tax)
    - Withdraw into phial in other hand with Withdraw Media (10% dust tax)
    - stores media in a block
    - Phiangle can be used to link them together
      - costs 3 charged, and 1 amel per 32 blocks of distance (media part not scalable)
      - phianglements cost 0 upkeep and have 0 tax on transfer of media between units
      - phiangled units only transfer on overflow or underflow
    - Dephiangle when you have long ass links that you don't wanna break by breaking the block
      - costs 3 charged
## Changes:
  - Amel Imbuement is datapack-friendlier now
  - Casting Rings can be worn in an extra slot on your off-hand as well now.
  - Decreased the base cost of Enchantments to 32 Amel.
  - Enchant Arms now gives you reach instead.
  - Envelop Feet In Amel enchantment has three levels now.
  - Envelop Feet In Amel enchantment no longer just nullifies but also cushions your fall.
  - Hastenature now has a +2.5 shard penalty if the target is Budding Amethyst.
  - Imbue Mind can now imbue into entities
    - This has potential ~~(to break my brain with overlapping recipes)~~
    - Currently it can be imbued into flayed villagers to un-flay them
  - Visible Distillation now tells you if an entity can see a block, unobstructed at a position.  
    Empty Distillation has the original behaviour for if you need it.
## Fixes:
  - Attempted to fix the Amel-Infused Gold Sword's animation not dripping down in third person.
    Mission (sorta) success. Now it looks kinda menacing because it's held so low???
  - Amel Jar's sprite's repositioning haunts me no more!
  - Fixed Duplication bug in Hexical interop ([#14](https://github.com/Real-Luxof/Lapisworks/issues/14))
  - Fixed Enchantments not taking Amel from your hotbar and trinkets n shit
  - FIXED ENHANCEMENTS AND ENCHANTMENTS NOT TRANSFERRING ACROSS DIMENSIONS!! ([#15](https://github.com/Real-Luxof/Lapisworks/issues/15))
  - Fixed Hastenature's book icon blending into the background.
  - Fixed Imbue Mind giving you the wrong mishap description
    (imbueable with Amel rather than a Simple Mind)
  - Fixed spell circles crashing for whatever reason! (thanks alexyzer) (issue: [#12](https://github.com/Real-Luxof/Lapisworks/issues/12), pull request: [#17](https://github.com/Real-Luxof/Lapisworks/pull/17))
  - Fixed the book and fixed it up a little too. Added some stuff as well.
    - Like stopping the book from not working when Hexical <2.0.0 was installed. ([#13](https://github.com/Real-Luxof/Lapisworks/issues/13))
    - Grammar
    - The added stuff
  - Fixed Teach Song being able to teach a Live Jukebox a song from any distance.
    (Also made it cost less media)
  - Fixed the Simple Mind Container looking FUCKED
## Interop:
  - Hexal
    - Added Enchanted Slipways
      It's a Simple Mind Infusion recipe (that costs Amel as well)
      They produce twice as many wandering wisps per second but they can't be turned into portals
      with Oneironaut
    - Simple Minds, when infused into the air, produce a wandering wisp
# 1.5.8
Locked in rah
## Additions:
- Amethyst/Chalk Rituals (pre-enlightenment)  
  - same-plane-only (no wall-to-floor or wall-to-ceiling etc.)
  - 5 patterns per chalk on ground max
  - plant little chunks of amethyst (like large amethyst buds but thinner)
    - tune them with Tune Media (costs amel)
    - rituals with the same frequency have ambit around that chunk of amethyst now
    - by default has 1 block of ambit, that being the tuned amethyst itself. this is the lower limit
      - Deposit Media to make the ambit larger (upper limit of 32 blocks maybe?)
      - Withdraw Media to make the ambit smaller
      - radius of ambit = sqrt(deposited media)
  - draw a BIG ASS fucking pattern on the ground
    - 9 blocks of chalk-with-pattern on the ground make a multiblock
    - media discount (20%?)
    - uses your staff stack and casts as you with your ambit
    - burns up after use
  - one-time rituals
    - right click with media item to absorb all of it into the ritual
      - alternatively Deposit Media
    - ritual burns chalk as it goes
    - ritual uses half your ambit (including gsent and enchsent)
    - hotswap tuned frequency and whether or not it casts as the starter
    - casting as the starter determines if it uses your ambit
  - multi-use rituals
    - right click the start block
    - ritual does not burn chalk as it goes
    - rituals uses 1/8th of your ambit (including gsent and enchsent)
    - tuned frequency and whether or not it casts as the caster must be configured in the start block
    - casting as the starter determines if it uses your ambit
- chalk_connectable block tag (all chalk attempts to connect to blocks in this tag)
- Enchanted Brewery
  - Imbue 10 Amel into a Brewing Stand
  - 1.5x blaze usage for 2x speed
  - Takes 1 amethyst dust per brew
- Erebus' Gambit
- Hadamard's Distillation
- Indigan Lapidary
  - alternative name: Noetic Lapidary
  - costs one amethyst shard in media, and at least two lapis in item form in your other hand
  - converts the two lapis into one amethyst (overflow of 1 lapis is consumed)
- Mintiest and Kitkat's Gambits (`for i in range(n):`)
- Scrying patterns for blocks added by the mod.
- Scrying lens overlays
- Simple Mind Infusions now have (kind of basic) datapacking support.
## Changes:
- 3D models of some blocks (and maybe an item or two?) look a little better
- Book reformatting and extra documentation and shit
  - e.g. Villager un-flaying is actually documented now
- Deposit Media and Withdraw Media work conveniently now
- Enchanted Sentinels actually use your base ambit instead of a flat 32 blocks around you
- Enchanted Slipways got changed to have nearly precisely 2x slipway wisp spawn rate  
  (as i intended them to have when i first made them)
- Fall Damage Resistance, you may now have 60 blocks of fall damage reduction with 3 levels
- Gold-Diamond Casting Ring has been deleted in favour of the Amel variant
- Mishap messages n shit
- Pattern name changes (particularly in the necklace RW patterns)
- Wizard lore is a lil diff now
- You can't read an Ancient Tome before you have gotten Lapisworks Research now
## Fixes:
- Amel Jar throwing the Amel in your other hand into the void if you attempted to withdraw
  with a full stack already there
- Ancient Tomes giving you the advancement anyway despite showing the message if you dont have lapis yet
- "Bug in the mod" mishaps
- Damage and movement speed enhancements stacking on world join (you need to kys ingame to reset tho)
- Deposit and Withdraw actually take doubles now, not just integers
- Enchanted Slipways having a tendency to move 0.01f more in +XYZ than -XYZ (lmao)
- Enchantments not carrying across dimensions (:broken_heart:)
- Enchantment Purification's order of arguments being flipped
- Finally fixed that Cradle bug with items for fucking real, holy fucking shit  
  IT STILL JUMPSCARES ME WHAT THE FUCK
- Handed Prison didn't drop blocks wtf
- Logspam begone!
- Shit should actually tell you when you don't have Fabric now
- Sieve Thoughts not working on a spell circle
- Withdraw bug
- You may no longer convert Lapis (+ your life force) into Amethyst Shards
  - It now pulls from your inventory
  - Also there's a spell for that now
## Interop:
- EMI
  - You can now see Imbue Amel, Mold Amel and (most) Simple Mind Infusion recipes in EMI
  - You can also see BeegInfusion recipes in EMI
- Hexical
  - the Cradle's item actually has a big hitbox now
  - the Media Jar and the Cradle are targets for Deposit Media, Withdraw Media and
    Condensed Media Prfn.
# 1.5.9
### Free me
- Heal your mind after breaking it. (Jacked O' Lantern finale)
- Alchemy/potion-brewing overhaul (I'm deadass)  
  herb stuff that leads to discovering Alchemy?  
  cauldron brewing! (definitely probably a part of herb stuff!)
- Oneironaut interop (you will shit your pants playing Lapisworks and you will like it)  
  so you like Subnautica?  
  - added the Congested Deep Noosphere
  - diving suit required to even exist in there (added bonus of not drowning)
    - or maybe just a Hexical Gasp spell daemon?
  - note to self: might have to fuck with world build height limit for this, as some creatures are
  simply gargantuan!
  - think this should be a progression of the enchanted slipway
- Valkyrien Skies interop
  air pocket in fully closed ship protects you from congested deep noosphere effects too
- better simple mind infusion datapacking
  - entity mind infusions!
  - predicates! like how vanilla advancement predicates work
  - displays can now be textures!
- Enchanted Scroll
  - It's literally a Hex Casting IDE.  
- Enchantweave? (formerly part of Hexic interop)
# 1.6.0
### And Alexander wept, for there were no more worlds to conquer.
- You can have four arms now (procrastination slain)
  - Your third and fourth arms can auto-cast 20x a second  
    Both must be devoted to auto-cast, but one can hold something (e.g. Focus, Amel-tuned Orb, etc.)
  - Your third and fourth arms can hold items
  - You may swap arms 1-2 with 3-4, and use them with mouse4-5 (yes mouse4 and mouse5, rebindable)
  - They can also do macro-work for you
  - bro tip for luxof: manningham mills lets you fuck with enums
- End overhaul
  - the ender dragon bossfight is fun now (nathan adding this to his pack is a secondary motivation)
  - there are new structures
  - lore
- Entity Construction
  - make a vessel
  - flay entities into it to get their AI
  - customize it's body and mind
- If not already done, EVERY PLAUSIBLE LAPISWORKS THING from "hm"

# 1.7.0
LAPISWORKS IS ON BOTH FORGE AND FABRIC NOW.


# hm
clairvoyance (future-seeing)  
noophaestus interop  
hexcasting media display interop  
iotic blocks interop  

addons that may have interesting interop ideas waiting to be had but idk yet:  
- hexcassettes? (`for i in range(n): enqueue(spell, tick_delay)`-like pattern?)
- Hexpose interop
  - spell to remove a status effect from the entity that has it
    - negative effects take power^2*time_multiplier dust to remove the status effect
    - positive effects take power^3*time_multiplier dust to remove the status effect
- hexchanting
  - modify the existing items to have spellbook-like functionality!
  - "i'm applying everything i've learned" or something
  - i'd hate to add superior stuff with amel infusion..
  - i don't want people downloading hexchanting just because of lapisworks
  - what do i do?
- complexhex
  - add qubits to hex casting (this is useless lmao)
- hexmachina
- slate works
  - make a loci that enhances cleric ambit around player to chalk circle levels!
  - costs amel
- ephemera
- hexdeco
- heartxxy
  - uhhh???
  - instant adult-ification spell, i guess?
  - like the counterpart to Hastenature, i mean
  - but Nurture exists tho...
- scryglass
  - add the ability to unfocus your mouse
  - add buttons
  - add text inputs?
- hexxyskies interop
  - ship variant of the jumpslate that jumps to the nearest ship in the specified radius
  - wisp-ify a ship by imbuing a simple mind into it
    - unlocks spells for fine-tuned ship movement?
  
much bigger phials  
~~ability to extend pattern and stack limit by expending media~~ gave that to hexthings  
  nvm hexthings threw it right back to me (infeasible for it)  
  0.01 dust per iota per pattern (meaning it stays that extended for that many patterns)  
  nvm oneironaut has it
computers lmao
- slab that you can use Craft Artifact on
- you can send iotas to computers with a spell (which costs more the longer the distance)
- sending iotas chunkloads
- has ambit over itself and the adjacent blocks
- casts on block update and iota sent

KING CRIMSON (so what part, exactly, of this is Lapisworks-y?)  
(P.S. even Miyu didn't want to do this. Are we deaduzz, chat?)
- select area and time
- area continues as normal for time
- now those entities (including players) are locked in to that movement
  - to prevent others from interfering use either an invisible barrier or do the same for around that area
    (but prevent caster from going there)
- caster is not locked in to that movement  

port twokai's ideal condition  
~~port hexxy dimensions~~ ~~pool and~~ scepticake took it  
Ra's Gambit
- think up something motherfucker
- this name is way too fucking cool not to use!  
Gene Editing (as an extension of the 4-arm-getting system) and Entity Creation
- making yourself a vampire is possible  

Make AVM staff as a variant of sorts of Hexical's Lightning Rod Staff
- affix items to it
- enchant it to make the fixations permanent
- when enchanted, it uses the item as a power like in AVM  

enchanted amethyst
- not the first time i've thought about it, maybe see where it goes  

COOL WIZARD DRIP  

backdrawn patterns
- free exquisite idea no one's done before, like per world pattern shapes  
  probably equally painful  
  this time the pain isn't in hexdoc, but in inline etc.!  
  fun for the whole family
- fix inline
- modify pattern drawing?
- there's an UncheckedHexPattern(? not sure if that's the name) now, use it
- might need a diff pattern iota to match it to stuff in a separate registry?
  - actually maybe mixin to PatternRegistryManifest (bro getting flooded)
- inverted color in book and grid + end of drawing to represent backstroke visually
- to represent it, the letter is "s"  

zone dstl projectile  

Impulse over time spell (schedules x velocity be applied in y time on an entity z)
- lets you do curve shots  
- name "Pull", "Sustain", "Impulse: Flow", "Tween", "Drive"?  
- Pull.

JIT compilation
- not sure if i can do this or if it'd even be worth it
- it would be very funny if i did add this though lmao
- also is a novel idea no one's added yet similar to PW pattern shapes
- reduce lag on cube eval hexes for example
- basically:
  - interpret the code of the pattern list
  - turn it into effects (hard-coded)
  - if there is an unsupported pattern, fail JIT
  - if this succeeds, the code just executes a bunch of effects now with the stack like a map
  - optimize stuff
    - Explode, Fireball, Wither Nadir, Clean Effects -> Explode, Fireball, incur some media cost
    - Raycast mantra raycast block stack manip raycast architect -> raycast to block + face  

make the addon more hexxy
- "mechanics should fit into Hex Casting like legos, combinable with other stuff and robust"  
  (paraphrased from Lani)  

per world pattern shapes picking between a few handmade ones is boring..  
add some randomness!  

jumpslate across space and time  
-# i don't even do drugs. what does this mean, past me?  

vv only if no one else is interested  
MASSIVE wizard towers! give you ambit + cost reduction + grid size (grid size toggleable)  

trinket that casts upon dropped  
right click to prime  

trinket that stops GTP item spillage and makes it half as cheap  
- "enderman's monocle"
- rub some amethyst dust on an eye of ender, then put it in an amel-iron-diamond case  

a trinket that shows a config screen for hexes it's primed to work for! it'll show
pre-configured-for-hex iotas and let you select their values, with a default value already present  

TODO:  
- Hex familiar that lets you interact with the Media Condensing Network at a range.
  - floating entity. like Terraria's flying piggy bank? or maybe just a wisp?
  - bind it to one linkable and it'll do all it's business with that one linkable
    - this linkable is it's entrypoint into any network, basically
    - can't make it auto-search for the nearest one because it needs to be not OP
  - you may now pull from that by rmb on the pet with a phial (attempts to fill whole phial)
  - you may also push to that by shift+rmb on the pet with a phial (attempts to drain whole phial)
  - costs about 2 amel per 32 blocks of distance (so free within 32 blocks)
  - i think it should be some kind of orb with a :3 face on it?
  - summon the pet via wearing a necklace for a minute
    - make it exclusive with the focus necklace :>
- Mind Control of entities in the game  
  (reality check: gang, how lost are we in the sauce?)
  - you have to un-flay with a Simple Mind first
  - you gain a "controllable" iota from the un-flaying.
    - in-lore, this is a set of mappings for what brain points do what, what makes the entity
      tick basically and also IO points for stuff (to store info for example)
    - in the event that you lose this iota, there is a pattern to get it from an entity
      that was un-flayed by you. this costs about a shard of amethyst
  - controllable movement
  - Deposit Media can be used to recharge a controllable
  - media limit of 64 dust
  - cannot overcast
  - credits to Sheppo from the Hex Casting discord server for these
    - they can be pets
    - VERY small ambit, at most 3 blocks and usually just 1 (by default too)
    - can have pre-set conditions to cast a hex, e.g. on hurt (so kind of like Hierophantics!)
      - not Sheppo: can only have one condition (in-lore: too much space occupied by condition and hex)
- Rote Brewery
  - Infuse a Simple Mind into a Brewing Stand
  - Can remember up to 5 potion recipes
  - Each write is permanent, stops brewing anything but remembered potions when at the limit
  - When a potion from memory is selected, takes items automatically.
  - ALWAYS takes 2 steps worth of time.
    Manual brewing (or teaching it) is a pain as each step takes twice as long.
    Automated brewing (or using what's been taught) is a breeze as N steps take only 2 to do.
- Hierophantics
  - Max experience fishermen villagers can be flayed into you  
    costs 32 amel and 10 charged amethyst  
    they only have the on_my_reference_found trigger, triggers when your reference is found in a stack of an offender within "range"  
      stack starts with a "guess" vector pointing from you to the enemy  
    has a "vigilance" attribute which can range from 0-3  
      0: no notification  
      1: chat notification  
      2: chat + audio notification  
      3: chat + on-screen + audio notification  
    they also have a "range" attribute (0-256)  
      the higher, the more inaccurate the guess (err_margin=range/4)  
      e.g. range=64 means guess can be 16 blocks from the offender  
      or range=256 means guess can be 64 blocks from the offender  
      err range is constant across all guesses  
        so if offender is 64 blocks away but your range is 256, err can be 0-64  
      however, if the offender is in your ambit the guess is always 100% precise  
  - Less than max experience fishermen can also be flayed into you  
    costs 16 amel and 10 charged amethyst  
    they are almost equivalent to the other mind  
    err=range/8 by default, none when offender is within ambit  
    range can only be 0-96  
    starts casting with an entity reference to the offender on the stack  
    has a 1/err chance of not detecting the offender  
  - "Jack" villager type  
    - villagers turn into "Jacks" when unflayed
    "Jacks" are jacks of all trades, and start with 2-3 levels of exp on every possible profession  
    - (but no trades until they pick one of those professions)
    - they're called "Jacks" because they're jacks of all trades
  - FUCKING UNICORNS
	  - IMBUE A SIMPLE MIND INTO A HORSE AND USE 128 AMEL
    - After being made, a Unicorn develops an affinity for you (and so is bound to you).
    - You can only have one Unicorn bound to you (any attempts to make more fail).
    - Unicorns are uncommonly seen, however they do appear around the player from time to time.  
      They VERY rarely spawn during the night.
    - Unicorns have a zone of influence around themselves with a radius of 32 blocks.
    - No hostile mobs can spawn in the presence of a unicorn, and any that spawn outside it's zone  
      of influence refuse to enter said zone of influence.
    - No patterns can execute within the zone of influence of a Unicorn, mishapping instead.  