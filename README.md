# Official HCR CORE
This core is designed and made by me. Any utils or public code will be credited in the [changelog](https://github.com/HackusatePvP/hcr-core/blob/main/CHANGELOG.md)
This is not a fork of any kind and any similarities between this and another core are just coincidental.

## 2023.01.a7
This is not a finished product **THERE WILL BE BUGS** this is a project that I work on my free time. Updates may not be frequent and maybe far in between.
A7 brought many new features, and many bug fixes. To keep updated with the project changes please check out the [changelog](https://github.com/HackusatePvP/hcr-core/blob/main/CHANGELOG.md).

**WARNING**
This version of the core removed support for Cove and added support for [Yuni](https://github.com/HackusatePvP/Yuni). Yuni's public version is not currently updated. The only other support is for [Aqua](https://builtbybit.com/resources/aquacore-core-manager-for-your-server.11118/).
Alternatively you could add your own support for other rank core plugins. View [YuniHook](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/hooks/YuniHook.java) to see how.

# Warning
This core is unfinished and untested. This core is not meant to be put into production until its fully completed.
Any and all issues you encounter will have to be fixed by you or another third party developer. Please do not create issues
within GitHub as they will most likely be ignored. This may change as the core comes closer to completion.

# Dependencies
This project is made to minimize as many dependencies as possible. The only required dependency will be for design only like a scoreboard/tab library. 
There are other dependency like core plugins to handle rank data, however these are completely optional.
- [Assemble](https://github.com/ThatKawaiiSam/Assemble) This is a really nice scoreboard api and gets the job done. Full credit the original creator.
- [AquaAPI](https://github.com/FaceSlap02/AquaCoreAPI) This is only needed if you plan on using AquaCore to handle rank data.
- [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/) This handles server to client packets.

# Style of HCF
This core is made for "hardcore" hcf servers. I never thought I would have to emphasize hardcore in a game-mode which is meant to be hardcore.
This core will never have any soft-core features like partner items, partner packages, ability items, ect. 
This core is made to make the game-mode as hard and as challenging as possible.

# FAQ
Q: This shit looks like iHCF.

A: The structure may be similar because of how I created the faction systems. I have factions running on super or "abstract" classes which makes creating different types of factions easier.
If you compare the code they are greatly different and any minor similarities are just a coincidence due to the same implementation style.

Q: Can I use this for my server?

A: Not recommended as it's not finished. If you do end up using this for your server, understand the rights section of this file.

# Rights
You have exclusive permission to modify use and redistribute the jar with restrictions. You are not allowed to re-sell this core whether it be modified or a fork. You are not allowed to claim any work that you did not do or create as your own.

# Compile a JAR
First you need to create a folder called `lib` in the projects main directory where the `pom.xml` file is located. Once the folder is created you need to download [ProtocolLib-4.6.jar](https://www.spigotmc.org/resources/protocollib.1997/) and [AquaCoreAPI.jar](https://github.com/FaceSlap02/AquaCoreAPI/tree/master/release) and place those jars inside the `lib` folder.


Locate and enter the directory `local` and run either `load.sh` or `load.bat` to compile dependencies into your local m2 repository. It is highly recommended reviewing any bat or sh file before executing the commands inside them.
Once the operation is completed simply run `mvn clean install` in the projects main directory where the pom.xml file is located. You will find the jar file in the `target` directory.


# Contact
If you have any questions you can contact me on discord. I have friend requests disable, but my dms are opened you can join my [discord](https://discord.gg/damKnGaqjK) server to send me a message.