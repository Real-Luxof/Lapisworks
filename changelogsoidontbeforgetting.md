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
huge thanks to the folks over at [Hexwoven](https://modrinth.com/server/hexwoven-server) for somehow putting with my BS and testing this mod :sob: (@oceanicdisturbance @teal_wolf_25 @meepoffaith @moss134 @rose.sylvanis)  

I'm delaying this update to the next level.  
Only the devs who can keep up with me...   
...Will get to see their 2000 emerald paycheck.  

## Additions:
- Amethyst/Chalk Rituals (pre-enlightenment)  
  - same-plane-only (no wall-to-floor or wall-to-ceiling etc.)
  - 5 patterns per chalk on ground max
  - tuneable amethyst
    - tune rituals to the same iota as they are tuned to to grant them some ambit around them.
    - radius of ambit = sqrt(deposited media in tuneable amethyst)
  - one-time rituals
    - half your ambit, can or can not cast as you (you pick), burns chalk.
  - multi-use rituals
    - always casts as you, has none of your ambit, doesn't burn chalk.
  - grand rituals
    - multi-block
    - uses your staff stack, has an animation, halves spell cost.
    - rmb with amel (does not consume) to make it burn up after use.
- block tags
  - chalk_connectable: all chalk SHOULD attempt to connect to blocks in this tag.
  - cant_place_chalk_on: the name.
  - onetimeritual_burn_blacklist: blacklists blocks from being burned by one time rituals when stepped over.
- chalk_connectable block tag (all chalk attempts to connect to blocks in this tag)
- Config! See your `.minecraft/config/lapisworks.json`.
- Disenchant
- Dealer's Purification (pick random, added because it has a cool name)
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
- Minimum and Maximum Distillations (it's just `min(n1, n2)` and `max(n1, n2)`) (i'm aware of the names :troll:)
- Mintiest and Kitkat's Gambits (`for i in range(n):`)
- Pull (spell)
  - Impulse constantly for X ticks after Y ticks
  - Ever wanted to do a curve shot?
  - Cost = Y/20 + speed\*X dust.
- Scrying patterns for blocks added by the mod.
- Scrying lens overlays
- Simple Mind Infusions now have (basic) datapacking support.
## Changes:
- 3D models of some blocks (and maybe an item or two?) look a little better
- Book reformatting and extra documentation and shit
  - e.g. Villager un-flaying is actually documented now
  - also thanks to @pool.critter and @rose.sylvanis for correcting a lot of my mistakes
- Deposit Media and Withdraw Media work conveniently now
- Enchanted Sentinels actually use your base ambit instead of a flat 32 blocks around you
- Enchanted Slipways got changed to have nearly precisely 2x slipway wisp spawn rate  
  (as i intended them to have when i first made them)
- Fall Damage Resistance, you may now have 60 blocks of fall damage reduction with 3 levels
- Gold-Diamond Casting Ring has been deleted in favour of the Amel variant  
  buff has also been removed
- May have done some funkies with the media spells (relocated and added entity support etc.)
- Mishap messages n shit
- Pattern name changes (particularly in the necklace RW patterns and also Inner Media Purification)
- Wizard lore is a lil diff (they are no longer outright evil as fuck)
- You can now disenchant yourself with the Enchant X patterns
- You can't read an Ancient Tome before you have gotten Lapisworks Research now
- You don't need any item required in your inv by a spell if in creative now
## Fixes:
- Amel Jar bug
- Ancient Tomes giving you the advancement anyway despite showing the message if you dont have lapis yet
- Attributes no longer compound when you relog (kys ingame to reset)
- "Bug in the mod" mishaps
- Cradle is fixed now (no dupes, no bugs on world load etc.)
- Deposit Media and Withdraw Media actually take doubles now, not just integers
- Deposit Media can no longer be healthcasted, nor can it draw from Inexhaustible Phials or Trinkets!
- Enchanted Slipways having a tendency to move 0.01f more in +XYZ than -XYZ (lmao)
- Enchantments not carrying across dimensions (:broken_heart:)
- Enchantment & Enhancement Purification's order of arguments being flipped
- Enhancement Purification actually trying to check the react attribute of a non-player
  - hey past me what the fuck is a react attribute
- Geode Dowser takes no media if you're in creative
- Handed Prison didn't drop its items wtf
- Imbue Amel prioritizes your left hand always instead of following whichever recipe came first  
  (was a bug where Imbue Amel would rather imbue a staff over a jar)
- Live Jukebox top texture good again
- Logspam begone!
- Media Condensing Units not dropping sometimes (e.g. with Break Block) and Empty Units not dropping in general
- Raycasting (Empty or Visible Distillation) would sometimes freeze your server (for alexyzer at least)
- Rebound Slate simple mind infusion recipe exists again
- Shit should actually tell you when you don't have Trinkets now
- Sieve Thoughts not working on a spell circle
- Sieve Thoughts not working sometimes (I think I fixed that)
- Villager un-flaying does not preserve levels anymore
- Visible Distillation and Empty Distillation should be a lot more optimized now (not that you'll notice it lmao)
- Withdraw Media bug
- You may no longer convert Lapis (+ your life force) into Amethyst Shards
  - It now pulls from your inventory
  - Also there's a spell for that now
## Interop:
- Hierophantics
  - Amalgamations!  
    - Ever wanted a counterspell?
  - "Jack" villager type  
    - villagers turn into "Jacks" when unflayed
    - "Jacks" start with 2-3 levels of exp on every profession  
    - (but no trades until they pick one of those professions)
    - they're called "Jacks" because they're jacks of all trades
- EMI
  - You can now see Imbue Amel, Mold Amel and (most) Simple Mind Infusion recipes in EMI
- Hexical
  - the Cradle's item actually has a big hitbox now
  - the Media Jar and the Cradle are targets for Deposit Media, Withdraw Media and
    Inner Media Prfn.
- Hexal
  - Wisps work with the media patterns
  - Wisps can hold an item now
- Valkyrien Skies
  - basically shit works
# 1.5.8.1-HOTFIX
- Change $(item)Diamond$() to just Diamond in that one title (zh_cn too)  
- Imbue Lapis takes lapis again (how this shit appears out of thin air will forever elude me)  
# 1.5.8.2
- Chinese translations by @chujik_ide
## Additions
- Deposit Media can start one-time rituals if used on chalk
- Impeti fully work normally with the media patterns now (but they can only store a maximum of 10000 dust according to the network)
- Ra's Gambit
  - every non-pattern iota henceforth gets pushed to the stack instead of mishapping
  - there's also a pattern to disable that
## Changes
- Book.
  - e.g. person who flayed themselves in the entry for Indigan Lapidary now no longer has specified pronouns
- Also changed the description for the Hallucination-Encrypted advancement
- Gave Media Condenser Units a tooltip
- Wands are called sceptres now
- Upgrading enchanted books has an actual limit now (decided by the config!)
## Fixes
- Deposit Media
- Withdraw Media
- Mintiest Gambit
- Enchant X spells didn't take media when you disenchanted with them
- Hexcessible showing unintended stuff (e.g. per world shape patterns before learning them)
- Robbie's Exaltation in HexBug (just gotta wait for that to update)
- Impeti mishapping when pulling from Condenser networks during Heimdall's Gambit (Oneironaut)
- Enchantments STILL wouldn't carry across dimensions (mfw one line fix)
- Enchanted Brewery wouldn't work with modded brewing recipes :tasque_waaanager:
- Catastrophic VAULT failure (took and gave more items than it should've)
- Enchanted Brewery looked like it still had the same potion pre-brewing on the client
- Some shit related to mixin mishap messages
# 1.5.8.3
### Additions
- Collars
  - dyeable
  - add a gold ingot to add a bell
    - jingles when the wearer moves too
  - add a focus to add iota holding functionality
  - add a charged amethyst to:
    - erase the floating patterns around you
    - disable invis particles
    - make the collar invisible while you're invisible
  - i'd let animals wear them but i have a major rendering skill issue (so it's next update)
- Dispel Conjureable and Cleaner's Purification
- Locator's Reflection (locate your enchanted sentinel)
- Patterns to exert more control over a Thoth's Gambit from within it
- Spherical Exaltation II
- Totem Necklace
  - no regen, fire prot, or anything. just effect clearing.
  - still revives you
  - 3 uses (starts with 1 when crafted)
  - rechargeable
### Changes
- Buk
- Chalk drawing screen respects Hex Casting's config
  - namely click to toggle drawing and the grid snap multiplier.
- Mishap messages (removed examples like "(e.g. a staff)")
- The meta-eval patterns can take patterns too now.
- Made the default enchantment limit in the config customizeable. You can also do math there now.
  - the comment in the config updates on its own when you run the game to reflect this
### Fixes
- A Charon in the meta-eval patterns won't exit the layer outside of them
- Crash on opening the staff grid with Hexcessible
- Crash on using Ancient Tomes in multiplayer
- Creative mode category no longer looks like it got hit by a tornado
- Dealer's Prfn didn't work
- Deposit Media bug??
- Enchanted Slipway spawn rate being 1x, not 2x a slipway's
- Erebus' Gambit played the spell cast sound and not the normal pattern cast sound on execute
- Placing a Ritus with the same data elsewhere in the world won't brick your world now
  - basically, Carryon works with the Ritus
- Potential Crash with Inner Media Prfn.
- Pull was free if you triggered it for 0 ticks
- Jumpslates and Rebound Slates weren't breakable and didn't drop their item
- Jumpslates didn't register anything but slates as a valid endpoint for Spell Circle ambit getting!
- Necklaces appear closer to your character
- "Oopsy woopsy" logspam BEGONE!
- Sieve Thoughts not detecting certain great spell pattern shapes
- Solid Color was not cheap to break via Break Block
- Cubic Exaltation was tweaking about the (0, 0, 0) coordinate
- Stopped Imbue Amel from taking a fuckton of Amel
- Weird poky bits from the sphere in Spherical Exaltation were only for the north and west sides
### Interop
- HexConnect (meaing Forge)
  It's easier to fix issues with Sinytra than it is to port the mod to both platforms
  - Apotheosis no longer prevents you from casting patterns when Lapisworks is in the pack
  - Fixed un-flaying villagers not un-flaying them

**NOTE FOR MIGRATORS:** While Jumpslates do register all spell circle components, you may have to replace them for this to work (because optimization). However, spell circle components placed after this update will never need to be replaced.
# 1.5.8.4
### Additions
- added stuff to collars
  - the bell now supports full physics
    - which has full physics with itself and movement
    - even rings on the internal bell's collisions
  - phianglement and media-containing functionality, maybe?
  - about the foci
    - these have special interactions when an animal wears them while they hold iotas
    - put a vector in it = animal wanna go there
    - put entity in it = animal attack
    - item entity = fox or allay pick up
  - add a name tag to:
    - allow writing MoreIotas String or Hexpose Text iotas to the collar via two new patterns
    - make the name that appears above you not be your own name but the String or Text written to the name tag.
- Enchanted Anvil? (Amvil?)
  - Lets you enchant beyond the maximum.
    - lets you combine beyond the usual maximum too.
    - maximum decided by the same max for Imbue Amel's enchantment effect.
  - Lets you enchant items with other items.
  - has a scrollable menu on the right to select the enchantments you wanna move over.
- Necklaces and collars can be on dogs, cats, foxes, or allays (right click)
  - Foci Necklaces make the NPC wearing them a target for Chronicler's Prfn and Gambit
  - Totem Necklaces DO in fact save your pets from death
### Changes
- Amalgamation Containers no longer look genuinely horrible
### Interop
- hexic
  - craft a collar and a nametag to make a collartag
    - changes your UUID
    - basically all entity iotas that point to your previous UUID 
  - craft a collar and a mediaweave to weave the cloth around it
    - nothing special, just acts as a mediaweave on top of being a collar
- HexConnect (meaning Forge)
  - Fixed chalk not connecting properly
# 1.5.9
### Free me
- [This](https://discord.com/channels/936370934292549712/950847275549229086/1479043488183095317)
  - what if custom gui (kindly kys me)
- Overenchanting?
- ~~Hexical interop~~ Taken by HexKeys with a cooler mechanism
  ~~- every key is telepathy-able~~
- Amethyst Piano
- Enchant item stack size (+64 every upgrade)
- Heal your mind after breaking it. (Jacked O' Lantern finale)
- Alchemy/potion-brewing overhaul (I'm deadass)  
  herb stuff that leads to discovering Alchemy?  
  cauldron brewing! (definitely probably a part of herb stuff!)
  "imbue amel into flowers to get seeds for special lapis herbs or something?" -Ragna
- Destroy Mind spell, reverts a simple mind infusion
- Oneironaut interop (you will shit your pants playing Lapisworks and you will like it)  
  so you like Subnautica?  
  - added the Congested Deep Noosphere
  - diving suit required to even exist in there (added bonus of not drowning)
    - or maybe just a Hexical Gasp spell daemon?
  - note to self: might have to fuck with world build height limit for this, as some creatures are
    simply gargantuan!
    - hey past Luxof, what the FUCK do you mean "FUCK WITH WORLD BUILD HEIGHT LIMIT"
    - THERE IS NO WAY IN HELL THE CREATURES NEED TO BE THAT BIG
    - CHRIST
  - think this should be a progression of the enchanted slipway
- Valkyrien Skies interop
  air pocket in fully closed ship protects you from congested deep noosphere effects too
- better simple mind infusion datapacking
  - entity mind infusions!
  - predicates! like how vanilla advancement predicates work
  - displays can now be textures!
- Enchanted Scroll
  - It's literally a Hex Casting IDE.  
- Enchanted Rifts' push effect now lessened by Knockback Resistance status effect
- Enchantweave? (formerly part of Hexic interop)
- Reroll enchantment selection in the table!
  - costs 1 charged am + 1 per previous cast
  - reset the additional cost by manually enchanting with an enchantment table
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
  - the ender dragon bossfight is fun now (pool adding this to its pack is a secondary motivation)
  - there are new structures
  - lore
- Entity Construction
  - make a vessel
  - flay entities into it to get their AI
  - customize it's body and mind
- If not already done, every plausibly Lapisworks-y thing from "hm"

# 1.7.0
LAPISWORKS IS ON BOTH FORGE AND FABRIC NOW. (is this even required?) (i mean Dryym should be able to play the mod right)


# hm
clairvoyance (future-seeing)  
noophaestus interop  
hexcasting media display interop  
iotic blocks interop  

possible interop:  
- hexical
  - give conjured color the ability to take mage block effects
  - Make AVM staff as a variant of sorts of Hexical's Lightning Rod Staff
    - affix items to it
    - enchant it to make the fixations permanent
    - when enchanted, it uses the item as a power like in AVM  
  - also add gloves with Hexical 2.0.0. they'll be two-handed charms that take half a second to clap.
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
  - "i got 47 more rounds in this 6 barrel shotgun" ahh idea search
  - I CAN ADD FUCKING GUN TURRETS
    - YOU HAVE TO CONTROL THEM BY HEX
- slate works
  - make a loci that enhances cleric ambit around player to chalk circle levels!
    - costs amel
  - Gemini Directrix
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
    - Nah, executes spells on physics ticks

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
- oh, makes embedding extremely easy!
Gene Editing (as an extension of the 4-arm-getting system) and Entity Creation
- making yourself a vampire is possible  


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
    - Explode, Fireball, give effect, take away effect -> explode, fireball, incur cost
    - Raycast mantra raycast block stack manip raycast architect =
        (many) -> (many, block (from archer) + face (from architect)) =
        raycast to block + face

make the addon more hexxy
- "mechanics should fit into Hex Casting like legos, combinable with other stuff and robust"  
  (paraphrased from Lani)  

per world pattern shapes picking between a few handmade ones is boring..  
add some randomness!  

jumpslate across space and time  
-# i don't even do drugs. what does this mean, past me?  

- vv only if no one else is interested  
  - MASSIVE multi-block wizard towers! give you ambit + cost reduction + grid size
    (grid size toggleable)  

trinket that casts upon dropped (so like death and shit)  
shift right click to prime/unprime  
right click to throw (when primed)

trinket that stops GTP item spillage and makes it half as cheap  
- "enderman's monocle"
- rub some amethyst dust on an eye of ender, then put it in an amel-iron-diamond case  

~~a trinket that shows a config screen for hexes it's primed to work for! it'll show~~
~~pre-configured-for-hex iotas and let you select their values, with a default value already present  ~~
Too late. HexIntent has that now.

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
  - Brewing a recipe it doesn't know is a pain as each step takes twice as long.
  - Brewing a recipe it DOES know has normal speed, but reduced ingredient cost.
    - cost reduction = 20% chance not to consume the ingredient being used  

potions!
  - potions of shine and dim
    - enable and disable the enchanted texture effect via pouring over an item
    - "pouring" is a shapeless crafting recipe with the item and the potion
  - chorus fruit potion
    - causes teleportation all over the place?
  - ender pearl potion
    - teleports you to where you consumed it after the effect runs out
    - milk bypasses teleportation
    - you can just. keep. adding redstone to make it longer
    - gives you Chaos when it teleports you, paralyzing you for a few seconds (like Terraria)  

so you know about the Ultra Instinct beam effect when Goku first achieves it right  
do that but for the chalk multiblock  
invert the color of the world inside the beam (shader)  
make it spin  
make it rectangular like a beacon's  
make it play a cool ass sound effect  
all that jazz  

put all this time shit in an addon  
call it Hex To The Future  

Hastenature is AOE now (and perhaps has a duration for which it boosts random tick rates?)  

FUCKING UNICORNS
  - IMBUE A SIMPLE MIND INTO A HORSE AND USE 64 AMEL
  - After being made, a Unicorn develops an affinity for you (and so is bound to you).
  - You can only have one Unicorn bound to you (any attempts to make more fail).
  - Unicorns are uncommonly seen, however they do appear around the player from time to time.  
    They VERY rarely spawn during the night.
  - Unicorns have a zone of influence around themselves with a radius of 32 blocks.
  - No hostile mobs can spawn in the presence of a unicorn, and any that spawn outside it's zone  
    of influence refuse to enter said zone of influence.
  - No patterns can execute within the zone of influence of a Unicorn, mishapping instead.  
  - see that one convo i had with Miyu (search up Tulpa on TechTastic)

wearable cypher/trinket/artifact trinket  
enchanted flowers?  

Slate Imprinter? Kind of obsolete since you can make it with foci neckli but uh why not?  

Lightningbending rework
- Hold a stance
- In this stance you are very likely to get hit by lightning
- Enchantment levels:
  - 1 = very likely to get hit by lightning in thunderstorms
  - 2 = instant hit in thunderstorms, likely in rain
  - 3 = instant hit in rain, likely in clear weather
  - 4 = instant hit always
- If you are hit with any lightning you redirect it where you're looking
- Yes, sideways lightning.  
  - past me, fuck yourself.

[This?](https://discord.com/channels/936370934292549712/1011455473528098857/1493387681684652153)  
with gloves, port the HexKinetics Swap Momentum spell and add a Swap Places and Swap Orientations spell.  
Witch broom to sit on as you fly.  

use "Apollo's Purification" somehow.  
Alder's Gambit (runs the provided pattern list, until it finds a specific pattern which you decide.)  
Glyph of Warding!  
