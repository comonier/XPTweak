# XPTweak 🧪 (v1.2)

**XPTweak** is an advanced experience (XP) management plugin for Minecraft, compatible with versions **1.19, 1.20, 1.21, and beyond**. Engineered for high performance, it fully supports **Spigot, Paper, and Folia (Multi-threading)** environments.

## 🚀 Key Features

### 1. Advanced XP Storage Systems
*   **Vanilla Storage (`/xpt max/lvl`):** Convert your XP levels into standard vanilla experience bottles.
*   **Partial Inventory Storage (`/xpt inv`):** **[NEW]** Fill only your available inventory space with bottles and keep the remaining XP in your experience bar.
*   **Precision NBT Storage (`/xptc`):** Create specialized bottles that store the **exact amount of points**. It uses NBT data to ensure no experience is lost.
*   **Inventory Safety:** The plugin prevents XP loss by checking inventory space before conversion. If full, players can use the `confirm` argument to drop items on the ground.

### 2. Economy & Auctions (Vault Integrated)
*   **XP Auctions:** Auction your XP levels to the highest bidder.
*   **Chat Control:** Toggle auction messages globally using `/xpt auc list`.
*   **Regressive Bidding:** Each new bid reduces the remaining auction time for a competitive environment.

### 3. Secure Transactions & Inspection
*   **Direct Donation:** Send XP levels directly to other players safely.
*   **XP Inspection (`/xpt inspect`):** **[NEW]** Check any player's exact level and total experience points. Available to everyone by default.
*   **Acceptance System:** Recipients must manually accept donations, preventing spam.

### 4. Automated & Manual XP Rain (WorldGuard)
*   **Area-Based Events:** Define regions via WorldGuard using the custom flag `xpt-rain allow`.
*   **Smart Announcements:** Custom localized messages for both In-game chat and Discord.
*   **Next Rain Timer (`/xpt time`):** **[NEW]** Precise countdown to the next event in `00h:00m:00s` format.
*   **Location Instructions:** **[NEW]** Customizable instructions (e.g., `/warp xp`) included in rain announcements.

### 5. Discord Integration (Webhooks)
*   Dedicated webhook support for **Donations**, **Auctions**, **XP Storage**, and **XP Rain** announcements.

---

## 🛠️ Commands & Permissions



| Command | Description | Permission | Default |
| :--- | :--- | :--- | :--- |
| `/xpt help` | Displays the localized help menu | `xptweak.user.base` | Everyone |
| `/xpt max [confirm]` | Stores all XP in bottles (drops if confirmed) | `xptweak.user.store` | Everyone |
| `/xpt inv` | Fills inventory with bottles; keeps rest in bar | `xptweak.user.store` | Everyone |
| `/xpt lvl <q> [confirm]` | Stores specific levels in bottles | `xptweak.user.store` | Everyone |
| `/xpt time` | Shows exact time until next XP Rain | `xptweak.user.base` | Everyone |
| `/xpt inspect <player>` | See a player's exact XP and points | `xptweak.user.base` | Everyone |
| `/xpt give <p> <q>` | Sends an XP donation request | `xptweak.user.give` | Everyone |
| `/xpt accept` | Accepts a pending donation | `xptweak.user.give` | Everyone |
| `/xpt auc <l> <p>` | Starts an XP auction | `xptweak.user.auction` | Everyone |
| `/xpt auc list` | Toggles auction messages ON/OFF | `xptweak.user.auction` | Everyone |
| `/xpt bid [x1\|x2]` | Places a bid on the active auction | `xptweak.user.auction` | Everyone |
| `/xptc <max\|lvl>` | Creates precision NBT bottles | `xptweak.user.custom` | OP |
| `/xpt rain <q> <t>` | Triggers a manual XP Rain event | `xptweak.admin.rain` | OP |
| `/xpt reload` | Reloads config and messages | `xptweak.admin.reload` | OP |

---

## 🌍 Localization (Multilingual)
The plugin supports fully translatable messages, including the help menu and time units:
*   **English (EN)** | **Portuguese (PT)** | **Spanish (ES)** | **Russian (RU)**

---

## 🔧 Installation & Setup
1.  Ensure you have **Vault** and **WorldGuard** installed.
2.  Drop `XPTweak.jar` into your `plugins` folder.
3.  For XP Rain setup:
    *   Select an area with WorldEdit.
    *   Define the region: `/rg define xp_arena`.
    *   Add the flag: `/rg flag xp_arena xpt-rain allow`.
4.  Configure the region name and `location-instruction` in `config.yml`.
5.  Restart your server.

---

## 💻 Technical Specifications
*   **Java:** 17 or higher.
*   **Database:** Support for SQLite (local) and MySQL (network).
*   **Multi-threading:** Full support for Folia regionized scheduling.
