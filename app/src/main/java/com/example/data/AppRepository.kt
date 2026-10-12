package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val db: AppDatabase) {

    val firebaseService = FirebaseFamilyService()

    val familyMembers: Flow<List<FamilyMemberEntity>> = db.familyMemberDao().getAllMembers()
    val memories: Flow<List<MemoryEntity>> = db.memoryDao().getAllMemories()
    val encryptedMemories: Flow<List<MemoryEntity>> = db.memoryDao().getEncryptedMemories()
    val storyChapters: Flow<List<StoryChapterEntity>> = db.storyChapterDao().getAllChapters()
    val notifications: Flow<List<NotificationEntity>> = db.notificationDao().getAllNotifications()

    suspend fun addMemory(memory: MemoryEntity) {
        db.memoryDao().insertMemory(memory)
        firebaseService.saveMemoryToFirestore(memory)
    }

    suspend fun deleteMemories(ids: List<String>) {
        db.memoryDao().deleteMemories(ids)
    }

    suspend fun moveMemoriesToAlbum(ids: List<String>, albumTag: String) {
        db.memoryDao().moveMemoriesToAlbum(ids, albumTag)
    }

    suspend fun seedInitialDataIfEmpty() {
        // Seed members
        val memberList = listOf(
            FamilyMemberEntity(
                id = "m1", name = "陈建国", englishName = "Chen Jianguo",
                role = "长辈 Patriarch", roleEn = "Patriarch", isElder = true,
                postsCount = 12, avatarResName = "img_anniversary_golden",
                activeTime = "3分钟前活跃 · 分享12条", activeTimeEn = "Active 3m ago · 12 posts", generation = 1
            ),
            FamilyMemberEntity(
                id = "m2", name = "李秀英", englishName = "Li Xiuying",
                role = "长辈 Matriarch", roleEn = "Matriarch", isElder = true,
                postsCount = 8, avatarResName = "img_elder_portrait",
                activeTime = "1小时前活跃 · 分享8条", activeTimeEn = "Active 1h ago · 8 posts", generation = 1
            ),
            FamilyMemberEntity(
                id = "m3", name = "陈小雅", englishName = "Xiao Ya",
                role = "管理员 Admin", roleEn = "Daughter · Admin", isElder = false,
                postsCount = 128, avatarResName = "img_elder_portrait",
                activeTime = "在线", activeTimeEn = "Online", generation = 2
            ),
            FamilyMemberEntity(
                id = "m4", name = "陈大明", englishName = "Da Ming",
                role = "长子 Son", roleEn = "Eldest Son", isElder = false,
                postsCount = 84, avatarResName = "img_family_reunion",
                activeTime = "昨天", activeTimeEn = "Yesterday", generation = 2
            ),
            FamilyMemberEntity(
                id = "m5", name = "陈浩", englishName = "Chen Hao",
                role = "长孙 Grandson", roleEn = "Grandson", isElder = false,
                postsCount = 22, avatarResName = "img_family_reunion",
                activeTime = "3天前", activeTimeEn = "3 days ago", generation = 3
            ),
            FamilyMemberEntity(
                id = "m6", name = "陈晶", englishName = "Chen Jing",
                role = "孙女 Granddaughter", roleEn = "Granddaughter", isElder = false,
                postsCount = 45, avatarResName = "img_family_reunion",
                activeTime = "5天前", activeTimeEn = "5 days ago", generation = 3
            ),
            FamilyMemberEntity(
                id = "m7", name = "小可", englishName = "Xiao Ke",
                role = "外孙女 Granddaughter", roleEn = "Granddaughter", isElder = false,
                postsCount = 19, avatarResName = "img_elder_portrait",
                activeTime = "1周前", activeTimeEn = "1 week ago", generation = 3
            )
        )
        db.familyMemberDao().insertMembers(memberList)

        // Seed memories with historical multi-decade timeline events
        val memoryList = listOf(
            MemoryEntity(
                id = "mem_2020_otd",
                title = "6年前的今天：颐和园夏日合影",
                titleEn = "6 Years Ago Today: Summer at Summer Palace",
                description = "2020年7月26日，清晨微风徐徐，全家人在昆明湖畔合影，荷花正盛。",
                descriptionEn = "July 26, 2020. Gentle morning breeze as the family posed near Kunming Lake.",
                location = "北京·颐和园",
                locationEn = "Beijing · Summer Palace",
                date = "2020-07-26",
                cameraModel = "Canon EOS R6 · 50mm f/1.8 · ISO 100",
                photoResNames = "img_family_reunion",
                likesCount = 92,
                commentsCount = 14,
                rating = 5.0f,
                isEncrypted = false,
                tags = "那年今日,2020,夏日,颐和园,6年前",
                eventName = "那年今日 2020",
                eventNameEn = "On This Day 2020",
                isHistoricalHighlight = true,
                isOfflineCached = true
            ),
            MemoryEntity(
                id = "mem_2015_otd",
                title = "11年前的今天：浩浩十岁生日宴",
                titleEn = "11 Years Ago Today: Hao's 10th Birthday",
                description = "2015年7月26日，长孙浩浩满10岁，爷爷亲自给他切蛋糕，画面无比温馨。",
                descriptionEn = "July 26, 2015. Grandson Chen Hao turned 10. Grandpa cut the cake with him.",
                location = "北京·全聚德",
                locationEn = "Beijing · Quanjude",
                date = "2015-07-26",
                cameraModel = "Nikon D810 · 35mm f/2.0 · ISO 400",
                photoResNames = "img_elder_portrait",
                likesCount = 110,
                commentsCount = 22,
                rating = 5.0f,
                isEncrypted = false,
                tags = "那年今日,2015,生日聚会,11年前",
                eventName = "那年今日 2015",
                eventNameEn = "On This Day 2015",
                isHistoricalHighlight = true,
                isOfflineCached = true
            ),
            MemoryEntity(
                id = "mem_1996_otd",
                title = "30年前的今天：泰山日出家庭登顶",
                titleEn = "30 Years Ago Today: Mt. Tai Sunrise Climb",
                description = "1996年7月26日，年轻时的建国带全家登顶泰山看日出，留下这幅珍贵历史瞬间。",
                descriptionEn = "July 26, 1996. Young Jianguo took the family to climb Mt. Tai for the sunrise.",
                location = "山东·泰山玉皇顶",
                locationEn = "Shandong · Mt. Tai Peak",
                date = "1996-07-26",
                cameraModel = "Contax T2 Film · 38mm f/2.8 · Kodak Gold 200",
                photoResNames = "img_anniversary_golden",
                likesCount = 135,
                commentsCount = 30,
                rating = 5.0f,
                isEncrypted = false,
                tags = "那年今日,1996,胶片老照片,30年前",
                eventName = "那年今日 1996",
                eventNameEn = "On This Day 1996",
                isHistoricalHighlight = true,
                isOfflineCached = true
            ),
            MemoryEntity(
                id = "mem_1972",
                title = "村口槐树下初相遇",
                titleEn = "First Meeting Under the Old Tree",
                description = "1972年金秋，建国第一次在村口老槐树下见到秀英。两颗年轻的心在那一刻悄然靠近。",
                descriptionEn = "Autumn 1972, Jianguo met Xiuying under the old locust tree for the first time.",
                location = "河北·老家村口",
                locationEn = "Hebei · Hometown Village",
                date = "1972-10-15",
                cameraModel = "黑白胶片 Black & White Film",
                photoResNames = "img_elder_portrait",
                likesCount = 88,
                commentsCount = 18,
                rating = 5.0f,
                isEncrypted = false,
                tags = "1972,初遇,历史瞬间,老照片",
                eventName = "初遇 1972",
                eventNameEn = "First Meeting 1972",
                isHistoricalHighlight = true,
                isOfflineCached = true
            ),
            MemoryEntity(
                id = "mem_1976",
                title = "桂花香里的简朴婚礼",
                titleEn = "Simple Wedding Scented with Osmanthus",
                description = "1976年秋天，没有华丽礼服，只有乡亲们的祝福与红双喜。这一牵手就是五十年。",
                descriptionEn = "In autumn 1976, with rustic warmth and blessing, they joined hands for a lifetime.",
                location = "河北·老宅",
                locationEn = "Hebei · Ancestral Home",
                date = "1976-09-20",
                cameraModel = "120底片 Vintage Camera",
                photoResNames = "img_anniversary_golden",
                likesCount = 102,
                commentsCount = 26,
                rating = 5.0f,
                isEncrypted = false,
                tags = "1976,婚礼,喜结良缘,历史里程碑",
                eventName = "喜结良缘 1976",
                eventNameEn = "Wedding Day 1976",
                isHistoricalHighlight = true,
                isOfflineCached = true
            ),
            MemoryEntity(
                id = "mem_2001",
                title = "银婚二十五周年相伴纪念",
                titleEn = "25th Silver Wedding Anniversary",
                description = "2001年，风雨二十五载，儿女已成才。全家人在照相馆拍下了第一张正式全家福。",
                descriptionEn = "2001, 25 years of devotion. The first formal studio family portrait.",
                location = "北京·照相馆",
                locationEn = "Beijing · Photo Studio",
                date = "2001-09-20",
                cameraModel = "Studio Kodak 35mm",
                photoResNames = "img_family_reunion",
                likesCount = 64,
                commentsCount = 12,
                rating = 4.9f,
                isEncrypted = false,
                tags = "2001,银婚,全家福,银婚纪念",
                eventName = "银婚 2001",
                eventNameEn = "Silver Wedding 2001",
                isHistoricalHighlight = true,
                isOfflineCached = true
            ),
            MemoryEntity(
                id = "mem2",
                title = "金婚五十周年盛大庆典",
                titleEn = "Golden 50th Anniversary Celebration",
                description = "2025年10月，相濡以沫半个世纪，五十载风雨同舟。儿孙满堂，盛大欢庆。",
                descriptionEn = "Half a century together through storm and rain. Surrounded by children and grandchildren.",
                location = "北京·王府井",
                locationEn = "Beijing · Wangfujing",
                date = "2025-10-18",
                cameraModel = "Sony A7M4",
                photoResNames = "img_anniversary_golden,img_family_reunion",
                likesCount = 128,
                commentsCount = 36,
                rating = 5.0f,
                isEncrypted = false,
                tags = "2025,金婚,50周年,历史巅峰",
                eventName = "金婚 2025",
                eventNameEn = "Golden Anniversary 2025",
                isHistoricalHighlight = true,
                isOfflineCached = true
            ),
            MemoryEntity(
                id = "mem1",
                title = "2026年四代同堂夏日聚会",
                titleEn = "2026 Four Generations Reunion",
                description = "今年的家庭聚会，爸妈笑得格外开心。岁月如歌，家是我们最温暖的归宿。",
                descriptionEn = "At this year's family reunion, mom and dad were extra happy. Time flies, family is our warmest harbor.",
                location = "北京·朝阳",
                locationEn = "Beijing · Chaoyang",
                date = "2026-06-18",
                cameraModel = "Canon R6",
                photoResNames = "img_family_reunion,img_anniversary_golden,img_elder_portrait",
                likesCount = 42,
                commentsCount = 8,
                rating = 4.8f,
                isEncrypted = false,
                tags = "2026,四代同堂,家庭聚会,最新",
                eventName = "四代同堂 2026",
                eventNameEn = "Four Generations 2026",
                isHistoricalHighlight = true,
                isOfflineCached = true
            ),
            MemoryEntity(
                id = "mem3",
                title = "祖父母私人珍藏秘档",
                titleEn = "Grandparents Private Vault Album",
                description = "保存老一辈年轻时的珍贵手信与加密家信",
                descriptionEn = "Preserving precious letters and private files of our grandparents",
                location = "加密云库",
                locationEn = "Encrypted Vault",
                date = "2024-05-12",
                cameraModel = "Scanned Archival",
                photoResNames = "img_anniversary_golden",
                likesCount = 15,
                commentsCount = 2,
                rating = 5.0f,
                isEncrypted = true,
                tags = "私密,加密,老照片",
                eventName = "私人保险库",
                eventNameEn = "Private Vault",
                isHistoricalHighlight = false,
                isOfflineCached = true
            )
        )
        db.memoryDao().insertMemories(memoryList)

        // Seed stories
        val chapterList = listOf(
            StoryChapterEntity(
                id = "ch1", chapterCode = "CH.01", year = "1972",
                title = "初遇", titleEn = "First Meeting",
                description = "那一年，建国第一次见到秀英，是在村口的老槐树下……",
                descriptionEn = "That year, Jianguo met Xiuying for the first time under the old tree...",
                voiceClipsCount = 3, photoCount = 12
            ),
            StoryChapterEntity(
                id = "ch2", chapterCode = "CH.07", year = "1976",
                title = "婚礼", titleEn = "Wedding Day",
                description = "那个秋天，桂花香飘满整个村庄，他牵起她的手……",
                descriptionEn = "That autumn, scented with sweet osmanthus, he took her hand...",
                voiceClipsCount = 5, photoCount = 28
            ),
            StoryChapterEntity(
                id = "ch3", chapterCode = "CH.12", year = "2026",
                title = "金婚之喜", titleEn = "Golden Anniversary",
                description = "五十载陪伴，执子之手与子偕老，儿孙齐聚欢庆金婚。",
                descriptionEn = "50 years of companionship, holding hands growing old together.",
                voiceClipsCount = 8, photoCount = 50
            )
        )
        db.storyChapterDao().insertChapters(chapterList)

        // Seed notifications
        val notifList = listOf(
            NotificationEntity(
                id = "n1", senderName = "陈妈妈",
                actionText = "喜欢了你的照片", actionTextEn = "liked your photo",
                detailText = "家庭年度聚会", timeAgo = "3分钟前", type = "like",
                avatarResName = "img_elder_portrait"
            ),
            NotificationEntity(
                id = "n2", senderName = "陈建国",
                actionText = "评论了你的相册", actionTextEn = "commented on album",
                detailText = "\"太美了！永远记得这一天\"", timeAgo = "1小时前", type = "comment",
                avatarResName = "img_anniversary_golden"
            ),
            NotificationEntity(
                id = "n3", senderName = "AI 助手",
                actionText = "为你生成了金婚精华视频", actionTextEn = "generated Golden Anniversary video for you",
                detailText = "点击查看", timeAgo = "2小时前", type = "system",
                avatarResName = "img_app_icon"
            ),
            NotificationEntity(
                id = "n4", senderName = "小明",
                actionText = "@提及了你", actionTextEn = "mentioned you",
                detailText = "妈妈你真美！", timeAgo = "昨天", type = "mention",
                avatarResName = "img_family_reunion"
            ),
            NotificationEntity(
                id = "n5", senderName = "小雅",
                actionText = "邀请你为家族树添加成员", actionTextEn = "invited you to add family tree member",
                detailText = "家族树管理", timeAgo = "2天前", type = "system",
                avatarResName = "img_elder_portrait"
            )
        )
        db.notificationDao().insertNotifications(notifList)
    }
}
