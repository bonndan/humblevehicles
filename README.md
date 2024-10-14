# Humble Vehicles Mod for Minecraft NeoForge


![logo](./humvee.png)

This is a NeoForge port of [LittleLogistics](https://littlelogistics.murad.dev/).

## Project goals

* port to 1.21, see https://gist.github.com/ChampionAsh5357/d895a7b1a34341e19c80870720f9880f
* learn Minecraft modding (+learn more Kotlin)
* have fun with rideable trains and ships in a vanilla setting
* use as much vanilla stuff a possible

## Features

- Trains with train cars
    - transport entities, items, fluids
    - routing AI
    - collision avoidance AI
- Tugs with barges
    - transport players, items, fluids
    - routing AI
    - fish automatically

### Differences to the original mod

- [Recipes](./recipes/readme.md) in markdown
- rideable vehicles plus **submarine**
- removed the rapid hopper and the vessel charger
- simplified routes

## License

### Source Code / java files

LGPLv3
https://www.gnu.org/licenses/lgpl-3.0.en.html

All assets are used with permission from the original authors of the Little Logistics project. 

New models made by [pega](https://www.fiverr.com/s/38y2BGL) are published under the same license as the source code.  

## Development

#### Adding an entity

* create a recipe in ModRecipeProvider
* create an Entity Model (e.g. SubmarineEntity)
* register the entity in ModEntityTypes
* register the model for rendering in ModClientEventHandler
* register events in ModEventBusEvents
* register a creative mode tab in ModItems
* add an entry in ModItemModelProvider