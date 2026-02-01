# NoticeExpress

A Minecraft Fabric mod for server announcements and notices with optional client-side GUI enhancement.

## Features

- 📢 **Server-side Notice Management** - Create, delete, pin, and manage server announcements
- 💾 **Persistent Storage** - SQLite database for reliable notice storage
- 🎨 **Dual Display Modes** - Chat-based notifications (fallback) and optional client GUI
- 📌 **Pin Important Notices** - Highlight critical announcements
- 🔐 **Permission System** - OP level 3 required for notice management
- ⚙️ **Configurable** - JSON-based configuration system

## Requirements

- **Minecraft**: 1.21.7
- **Fabric Loader**: 0.18.4 or higher
- **Fabric API**: 0.129.0+1.21.7 or higher
- **Java**: 21 or higher

## Installation

### Server-side (Required)
1. Download the mod JAR file
2. Place it in your server's `mods` folder
3. Start the server
4. Configuration will be generated at `config/noticeexpress/config.json`

### Client-side (Optional)
1. Download the same mod JAR file
2. Place it in your client's `mods` folder
3. Enjoy enhanced GUI features (coming soon)

## Commands

All commands start with `/notice`:

### Management Commands (OP Level 3+)

#### Publish a Notice
```
/notice publish <title> <content>
```
**Example:**
```
/notice publish "Server Maintenance" "The server will be down for maintenance on Saturday from 2-4 PM."
```

#### Delete a Notice
```
/notice delete <id>
```
**Example:**
```
/notice delete 1
```

#### Pin a Notice
```
/notice pin <id>
```
**Example:**
```
/notice pin 2
```

#### Unpin a Notice
```
/notice unpin <id>
```
**Example:**
```
/notice unpin 2
```

### Viewing Commands (All Players)

#### List All Notices
```
/notice list
```
Shows a compact list of all notices with their IDs and titles.

#### Show Full Notices
```
/notice show
```
Displays all notices with full content in chat.

## Chat Display Format

When viewing notices in chat, they appear in the following format:

```
[Publisher] [YYYY/MM/DD HH:mm] [PINNED]
Title (in gold, bold)
Content (in white, supports multiple lines)
```

**Example:**
```
[Admin] [2026/02/02 12:30] [PINNED]
Server Maintenance
The server will be down for maintenance on Saturday from 2-4 PM. 
Please save your progress before then.
```

## Configuration

Configuration file location: `config/noticeexpress/config.json`

```json
{
  "serverTitle": "Server Announcements",
  "databasePath": "config/noticeexpress/notices.db"
}
```

### Configuration Options

- **serverTitle**: The title displayed in the client GUI (default: "Server Announcements")
- **databasePath**: Path to the SQLite database file (default: "config/noticeexpress/notices.db")

## Database

NoticeExpress uses SQLite for persistent storage. The database is automatically created and managed.

### Database Schema

```sql
CREATE TABLE notices (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    publisher TEXT NOT NULL,
    publisher_uuid TEXT NOT NULL,
    content TEXT NOT NULL,
    timestamp INTEGER NOT NULL,
    is_pinned INTEGER NOT NULL DEFAULT 0,
    created_at INTEGER NOT NULL
)
```

## Permissions

The mod uses Minecraft's built-in OP system:

- **OP Level 3+**: Can publish, delete, pin, and unpin notices
- **All Players**: Can view notices using `/notice list` and `/notice show`

## Client GUI (Coming Soon)

The optional client-side mod will provide an enhanced GUI with:

- 📋 Scrollable notice list
- 🎴 Beautiful notice cards
- 📌 Visual pinned indicators
- ✨ Expand/collapse functionality
- 🖱️ Click-to-interact interface

## For Developers

### Building from Source

1. Clone the repository:
```bash
git clone https://github.com/0x002500/NoticeExpress.git
cd NoticeExpress
```

2. Build the mod:
```bash
./gradlew build
```

3. Find the compiled JAR in `build/libs/`

### Project Structure

```
src/
├── main/
│   ├── java/top/orderly/noticeexpress/
│   │   ├── NoticeExpress.java          # Main mod class
│   │   ├── command/
│   │   │   └── NoticeCommand.java      # Command registration and handlers
│   │   ├── config/
│   │   │   └── ModConfig.java          # Configuration management
│   │   ├── database/
│   │   │   ├── DatabaseManager.java    # SQLite connection manager
│   │   │   └── NoticeRepository.java   # CRUD operations
│   │   ├── model/
│   │   │   └── Notice.java             # Notice entity
│   │   └── util/
│   │       ├── ChatNotificationFormatter.java  # Chat formatting
│   │       ├── PermissionChecker.java          # Permission utilities
│   │       └── TimeFormatter.java              # Time formatting
│   └── resources/
│       ├── fabric.mod.json             # Mod metadata
│       └── assets/noticeexpress/
│           └── icon.png                # Mod icon
└── client/
    └── java/top/orderly/noticeexpress/
        ├── NoticeExpressClient.java    # Client initialization
        └── ui/                         # GUI components (coming soon)
```

### API Usage

#### Creating a Notice Programmatically

```java
import top.orderly.noticeexpress.NoticeExpress;
import top.orderly.noticeexpress.model.Notice;

Notice notice = new Notice();
notice.setTitle("My Notice");
notice.setPublisher("System");
notice.setPublisherUuid(UUID.randomUUID());
notice.setContent("This is a programmatically created notice.");

NoticeExpress.getNoticeRepository().createNotice(notice);
```

#### Retrieving Notices

```java
import top.orderly.noticeexpress.NoticeExpress;
import top.orderly.noticeexpress.model.Notice;
import java.util.List;

// Get all notices
List<Notice> allNotices = NoticeExpress.getNoticeRepository().getAllNotices();

// Get a specific notice by ID
Notice notice = NoticeExpress.getNoticeRepository().getNoticeById(1);

// Get notices since a timestamp
long timestamp = System.currentTimeMillis() - 86400000; // Last 24 hours
List<Notice> recentNotices = NoticeExpress.getNoticeRepository().getNoticesSince(timestamp);
```

## Changelog

### Version 1.0.0 (Current)
- ✅ Initial release
- ✅ Server-side notice management
- ✅ SQLite database integration
- ✅ Chat-based notifications
- ✅ Permission system
- ✅ Configuration system
- ✅ Integer-based notice IDs
- ✅ Title and content separation

### Upcoming Features
- 🔜 Client-side GUI
- 🔜 Network packet communication
- 🔜 Player join notifications
- 🔜 Localization support (English & Chinese)

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'feat: add some amazing feature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Commit Message Convention

This project follows [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` - New features
- `fix:` - Bug fixes
- `docs:` - Documentation changes
- `style:` - Code style changes (formatting, etc.)
- `refactor:` - Code refactoring
- `test:` - Adding or updating tests
- `chore:` - Maintenance tasks

## License

This project is licensed under the Apache-2.0 License - see the [LICENSE](LICENSE) file for details.

## Support

If you encounter any issues or have questions:

1. Check the [Issues](https://github.com/0x002500/NoticeExpress/issues) page
2. Create a new issue if your problem isn't already reported
3. Provide as much detail as possible (Minecraft version, mod version, error logs, etc.)

## Acknowledgments

- Built with [Fabric](https://fabricmc.net/)
- Uses [SQLite JDBC](https://github.com/xerial/sqlite-jdbc) for database operations
- Inspired by various server announcement plugins

---

**Made with ❤️ for the Minecraft community**