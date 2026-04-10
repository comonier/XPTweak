# XPTweak 🧪

**XPTweak** is an advanced experience (XP) management plugin for Minecraft, compatible with versions **1.19, 1.20, 1.21, and beyond**. Engineered for high performance, it fully supports **Spigot, Paper, and Folia (Multi-threading)** environments.

## 🚀 Key Features

### 1. XP Storage Systems
*   **Vanilla Storage (`/xpt`):** Quickly convert your XP levels into standard vanilla experience bottles.
*   **Precision NBT Storage (`/xptc`):** Create specialized bottles that store the **exact amount of points**. It uses NBT data to ensure no experience is lost during conversion.

### 2. Economy & Auctions (Vault Integrated)
*   **XP Auctions:** Auction your XP levels to the highest bidder.
*   **Chat Control:** Players who want to avoid auction spam can toggle auction messages globally using `/xpt auc list`.
*   **Regressive Bidding:** Each new bid can reduce the remaining auction time, creating a fast-paced competitive environment.

### 3. Secure Transactions
*   **Direct Donation:** Send XP levels directly to other players safely.
*   **Acceptance System:** Recipients must manually accept donations, preventing unwanted XP or inventory spam.

### 4. Automated & Manual XP Rain (WorldGuard)
*   **Area-Based Events:** Define regions via WorldGuard using the custom flag `xpt-rain allow`.
*   **Visual Effect:** XP orbs fall randomly from the sky within the defined region, providing a realistic "rain" effect.

### 5. Discord Integration (Webhooks)
*   Dedicated webhook support for **Donations**, **Auctions**, and **XP Rain** announcements.

---

## 🛠️ Commands & Permissions


| Command | Description | Permission | Default |
| :--- | :--- | :--- | :--- |
| `/xpt help` | Displays the customizable help menu | `xptweak.user.base` | Everyone |
| `/xpt max` | Stores all XP in vanilla bottles | `xptweak.user.store` | Everyone |
| `/xpt lvl <qty>` | Stores specific levels in bottles | `xptweak.user.store` | Everyone |
| `/xpt give <p> <q>` | Sends an XP donation request | `xptweak.user.give` | Everyone |
| `/xpt accept` | Accepts a pending donation | `xptweak.user.give` | Everyone |
| `/xpt auc <l> <p>` | Starts an XP auction | `xptweak.user.auction` | Everyone |
| `/xpt auc list` | Toggles auction messages ON/OFF | `xptweak.user.auction` | Everyone |
| `/xpt bid [x1\|x2]` | Places a bid on the active auction | `xptweak.user.auction` | Everyone |
| `/xptc <max\|lvl>` | Creates precision NBT bottles | `xptweak.user.custom` | OP |
| `/xpt rain <q> <t>` | Triggers a manual XP Rain event | `xptweak.admin.rain` | OP |
| `/xpt reload` | Reloads config and messages | `xptweak.admin.reload` | OP |

---

## 🌍 Supported Languages
The plugin automatically detects or uses the language defined in `config.yml`:
*   **English (EN)**
*   **Portuguese (PT)**
*   **Spanish (ES)**
*   **Russian (RU)**

---

## 🔧 Installation & Setup
1.  Ensure you have **Vault** and **WorldGuard** installed.
2.  Drop `XPTweak.jar` into your `plugins` folder.
3.  For XP Rain setup:
    *   Select an area with WorldEdit.
    *   Define the region: `/rg define xp_arena`.
    *   Add the flag: `/rg flag xp_arena xpt-rain allow`.
4.  Configure the region name in `config.yml`.
5.  Restart your server.

---

## 💻 Technical Specifications
*   **Java:** 17 or higher.
*   **Database:** Support for SQLite (local) and MySQL (network).
*   **Multi-threading:** Full support for Folia regionized scheduling.
