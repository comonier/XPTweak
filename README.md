# XPTweak 🧪

**XPTweak** is an advanced and universal experience (XP) management plugin for Minecraft, compatible with versions **1.19, 1.20, 1.21, and beyond**. Engineered for performance and scalability, it fully supports **Spigot, Paper, and Folia (Multi-threading)** environments.

## 🚀 Key Features

### 1. XP Storage Systems
*   **Vanilla Storage (`/xpt`):** Quickly convert your XP levels into standard vanilla experience bottles.
*   **Custom NBT Storage (`/xptc`):** Create specialized bottles that store **exact level counts**. These bottles use NBT data to ensure precision and display the creator's name.

### 2. Economy & Auctions (Vault Integrated)
*   **XP Auctions:** Auction your XP levels to the highest bidder.
*   **Dynamic Bidding:** Featuring a regressive timer system where each bid can reduce the remaining time, creating a fast-paced auction environment.
*   **Vault Integration:** Fully integrated with the server's economy for bidding and payouts.

### 3. Secure Transactions
*   **Peer-to-Peer Donation:** Send XP levels directly to other players.
*   **Safety First:** Recipients must manually accept donations, preventing unwanted XP or inventory spam.

### 4. Automated & Manual XP Rain (WorldGuard)
*   **Area-Based Events:** Define large regions (e.g., 8x8 chunks) using WorldGuard flags (`xpt-rain allow`).
*   **Smart Spawning:** Orbs spawn randomly across the region, falling from the sky to provide a "rain" effect.
*   **Independent Logic:** Works even without players online; orbs will wait in the chunk until it's loaded.

### 5. Discord Integration (Webhooks)
*   Dedicated webhook support for **Donations**, **Auctions**, and **XP Rain** announcements.
*   Keep your community engaged by broadcasting in-game events directly to Discord channels.

### 6. Reliability & Admin Tools
*   **Database Support:** Choose between **SQLite** (local) or **MySQL** (external) for data persistence and logging.
*   **Safe Reload:** Robust configuration loader that detects syntax errors without breaking the plugin or flooding the console.
*   **Death Protection:** Optional feature to drop 100% of a player's XP as a custom bottle upon death.

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
| `/xpt bid [x1\|x2]` | Bids on an active auction | `xptweak.user.auction` | Everyone |
| `/xptc <max\|lvl>` | Creates custom NBT XP bottles | `xptweak.user.custom` | OP |
| `/xpt rain <q> <t>` | Triggers a manual XP Rain event | `xptweak.admin.rain` | OP |
| `/xpt reload` | Reloads config and messages | `xptweak.admin.reload` | OP |

---

## 🌍 Supported Languages
The plugin is fully internationalized. Change the `language` setting in `config.yml`:
*   **English (EN)**
*   **Portuguese (PT)**
*   **Spanish (ES)**
*   **Russian (RU)**

---

## 🔧 Installation & Setup
1.  **Dependencies:** Ensure you have **Vault** and **WorldGuard** installed.
2.  **Plugin Placement:** Drop `XPTweak.jar` into your `plugins` folder.
3.  **WorldGuard Setup:** 
    *   Select an area with WorldEdit (`//wand`).
    *   Define a region: `/rg define xp_arena`.
    *   Add the flag: `/rg flag xp_arena xpt-rain allow`.
4.  **Configuration:** Update the `region-name` in `config.yml` to match your region.
5.  **Restart:** Restart your server and enjoy!

---

## 💻 Technical Specifications
*   **Java Version:** 17+
*   **Supported Platforms:** Spigot, Paper, Folia.
*   **Supported Versions:** 1.19.x, 1.20.x, 1.21.x (and subversions).
