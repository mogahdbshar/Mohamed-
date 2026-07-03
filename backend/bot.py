import os
import json
import time
import threading
from flask import Flask, jsonify, request
import telebot
from telebot import types

# -------------------------------------------------------------
# 1. Initialization and Configuration
# -------------------------------------------------------------

CONFIG_FILE = "config.json"
TELEMETRY_FILE = "telemetry.json"

DEFAULT_CONFIG = {
    "min_app_version": 1,
    "latest_app_version": 1,
    "update_url": "https://t.me/dstwrtv_channel",
    "update_message": "يتوفر إصدار جديد يحتوي على قنوات إضافية وإصلاح للمشاكل الحالية. يرجى التحديث فوراً لتجنب انقطاع البث!",
    "enable_ads": False,
    "ad_provider": "none",
    "ad_banner_id": "",
    "ad_interstitial_id": "",
    "custom_ad_image_url": "",
    "custom_ad_click_url": "",
    "custom_ad_display_location": "none",
    "announcement_show": False,
    "announcement_title": "تنبيه هام",
    "announcement_message": "",
    "announcement_type": "banner",
    "announcement_skippable": True,
    "enable_crypto": True,
    "crypto_algorithm": "AES",
    "user_agent_override": "",
    "referer_override": "",
    "allowed_countries": "ALL",
    "app_accent_color": "",
    "support_button_url": "https://t.me/dstwrtv_channel",
    "support_button_visible": True,
    "developer_category_name": "باقات المطور",
    "show_onboarding_always": False,
    "hidden_categories": "",
    "hidden_channels": "",
    "hide_all_channels": False,
    "hidden_tabs": "",
    "hide_all_developer_options": False,
    "enable_developer_channels": True,
    "telemetry_url": ""
}

def load_config():
    if os.path.exists(CONFIG_FILE):
        try:
            with open(CONFIG_FILE, 'r', encoding='utf-8') as f:
                return {**DEFAULT_CONFIG, **json.load(f)}
        except Exception:
            return DEFAULT_CONFIG.copy()
    return DEFAULT_CONFIG.copy()

def save_config(cfg):
    try:
        with open(CONFIG_FILE, 'w', encoding='utf-8') as f:
            json.dump(cfg, f, ensure_ascii=False, indent=4)
    except Exception as e:
        print(f"Error saving config: {e}")

def load_telemetry():
    if os.path.exists(TELEMETRY_FILE):
        try:
            with open(TELEMETRY_FILE, 'r', encoding='utf-8') as f:
                return json.load(f)
        except Exception:
            return {}
    return {}

def save_telemetry(data):
    try:
        with open(TELEMETRY_FILE, 'w', encoding='utf-8') as f:
            json.dump(data, f, ensure_ascii=False, indent=4)
    except Exception as e:
        print(f"Error saving telemetry: {e}")

config = load_config()
telemetry_data = load_telemetry()

# Lock to ensure thread-safety when accessing files/memory
data_lock = threading.Lock()

# Try to load .env from the current directory or parent directory
def load_dotenv():
    for path in [".env", "../.env", "backend/.env"]:
        if os.path.exists(path):
            try:
                with open(path, "r", encoding="utf-8") as f:
                    for line in f:
                        line = line.strip()
                        if line and not line.startswith("#") and "=" in line:
                            key, val = line.split("=", 1)
                            os.environ[key.strip()] = val.strip()
            except Exception as e:
                print(f"Error loading .env: {e}")

load_dotenv()

# Environment Credentials
BOT_TOKEN = os.environ.get("BOT_TOKEN", "")
ADMIN_ID = os.environ.get("ADMIN_ID", "") # Allowed admin Telegram user ID (strictly secure)

bot = None
if BOT_TOKEN:
    bot = telebot.TeleBot(BOT_TOKEN)

app = Flask(__name__)

# -------------------------------------------------------------
# 2. Flask REST Endpoints (For Android App Integration)
# -------------------------------------------------------------

@app.route('/', methods=['GET'])
def index():
    return jsonify({
        "status": "online",
        "service": "DSTWRTV Remote Config & Telemetry Control Center",
        "endpoints": {
            "config": "/config",
            "telemetry": "/telemetry (POST)"
        }
    })

@app.route('/config', methods=['GET'])
def get_config():
    with data_lock:
        cfg = config.copy()
        # Automatically point telemetry_url to this host dynamically if not hardcoded
        if not cfg.get("telemetry_url"):
            host_url = request.host_url.rstrip('/')
            cfg["telemetry_url"] = f"{host_url}/telemetry"
        return jsonify(cfg)

@app.route('/telemetry', methods=['POST'])
def post_telemetry():
    try:
        data = request.json
        if not data:
            return jsonify({"error": "No payload received"}), 400

        install_id = data.get("installation_id")
        if not install_id:
            return jsonify({"error": "Missing installation_id"}), 400

        with data_lock:
            global telemetry_data
            telemetry_data[install_id] = {
                "last_seen": int(time.time()),
                "status": data.get("status", "unknown"),
                "device_model": data.get("device_model", "Unknown Device"),
                "os_version": data.get("os_version", "Android unknown"),
                "app_version": data.get("app_version_name", "2.1.0"),
                "active_channel": data.get("active_channel", None)
            }
            save_telemetry(telemetry_data)

        return jsonify({"status": "acknowledged", "timestamp": int(time.time())})
    except Exception as e:
        return jsonify({"error": str(e)}), 500

# -------------------------------------------------------------
# 3. Telegram Bot Control Mechanisms (Protected Admin Layer)
# -------------------------------------------------------------

def is_admin(message):
    if not ADMIN_ID:
        # If no ADMIN_ID is set in environment, allow any user temporarily to let them register
        return True
    return str(message.from_user.id) == str(ADMIN_ID)

if bot:
    @bot.message_handler(commands=['start'])
    def send_welcome(message):
        global ADMIN_ID
        welcome_text = (
            "🛠️ *لوحة تحكم تطبيق DSTWRTV الذكية*\n\n"
            "مرحباً بك في نظام التحكم السحابي الآمن الخاص بالتطبيق. يمكنك التحكم بكافة الخصائص مباشرة من هنا.\n\n"
        )
        if not ADMIN_ID:
            # Let the first person who starts the bot know their user ID to set as ADMIN_ID
            welcome_text += (
                f"⚠️ *ملاحظة أمنية:* لم يتم تعيين معرف المسؤول (ADMIN_ID) في البيئة بعد.\n"
                f"معرف حسابك الحالي هو: `{message.from_user.id}`\n"
                f"يرجى إضافته إلى متغيرات البيئة باسم `ADMIN_ID` لضمان حماية اللوحة عن بقية المستخدمين."
            )
            bot.reply_to(message, welcome_text, parse_mode="Markdown")
        else:
            if not is_admin(message):
                bot.reply_to(message, "❌ *عذراً!* هذا البوت مخصص للمسؤول المطور فقط ولا يمكن لغيرك الوصول للوحة التحكم للخصوصية والأمان.", parse_mode="Markdown")
                return
            
            welcome_text += "الرجاء استخدام الأزرار أدناه أو الأوامر لإدارة قنوات وخصائص التطبيق."
            
            # Custom keyboard layout
            markup = types.ReplyKeyboardMarkup(resize_keyboard=True, row_width=2)
            btn_status = types.KeyboardButton("📊 حالة البث والإحصائيات")
            btn_toggle = types.KeyboardButton("🔌 إيقاف/تشغيل كافة القنوات")
            btn_clear_hidden = types.KeyboardButton("🔄 إعادة ضبط كافة الفلاتر")
            btn_devices = types.KeyboardButton("📱 الأجهزة المتصلة الآن")
            btn_toggle_dev = types.KeyboardButton("🛠️ إخفاء/إظهار قنوات التطبيق")
            markup.add(btn_status, btn_toggle)
            markup.add(btn_devices, btn_clear_hidden)
            markup.add(btn_toggle_dev)
            
            bot.send_message(message.chat.id, welcome_text, reply_markup=markup, parse_mode="Markdown")

    @bot.message_handler(func=lambda msg: msg.text == "📊 حالة البث والإحصائيات")
    def btn_status_handler(message):
        if not is_admin(message): return
        show_current_status(message.chat.id)

    @bot.message_handler(func=lambda msg: msg.text == "🔌 إيقاف/تشغيل كافة القنوات")
    def btn_toggle_handler(message):
        if not is_admin(message): return
        with data_lock:
            global config
            config["hide_all_channels"] = not config["hide_all_channels"]
            save_config(config)
            status_str = "❌ تم إيقاف عرض كافة القنوات وتفعيل شاشة الصيانة بالتطبيق" if config["hide_all_channels"] else "✅ تم إعادة تفعيل كافة القنوات وبدء البث بنجاح"
        bot.send_message(message.chat.id, f"⚡ *تغيير حالة البث:*\n\n{status_str}", parse_mode="Markdown")

    @bot.message_handler(func=lambda msg: msg.text == "🛠️ إخفاء/إظهار قنوات التطبيق")
    def btn_toggle_dev_options_handler(message):
        if not is_admin(message): return
        with data_lock:
            global config
            is_hidden = not config.get("hide_all_developer_options", False)
            config["hide_all_developer_options"] = is_hidden
            config["enable_developer_channels"] = not is_hidden
            save_config(config)
            status_str = "❌ تم إخفاء قنوات التطبيق المدمجة بالكامل وإخفاء إعداداتها وأزرارها من التطبيق." if is_hidden else "✅ تم إعادة إظهار قنوات التطبيق المدمجة وإتاحة إعداداتها وأزرارها في التطبيق."
        bot.send_message(message.chat.id, f"⚡ *تغيير حالة قنوات التطبيق:*\n\n{status_str}", parse_mode="Markdown")

    @bot.message_handler(func=lambda msg: msg.text == "🔄 إعادة ضبط كافة الفلاتر")
    def btn_reset_filters_handler(message):
        if not is_admin(message): return
        with data_lock:
            global config
            config["hidden_categories"] = ""
            config["hidden_channels"] = ""
            config["hidden_tabs"] = ""
            config["hide_all_channels"] = False
            save_config(config)
        bot.send_message(message.chat.id, "🔄 *تم إعادة ضبط الفلاتر وحجب القنوات والتبويبات بنجاح!* جميع القنوات ظاهرة الآن بالتطبيق.", parse_mode="Markdown")

    @bot.message_handler(func=lambda msg: msg.text == "📱 الأجهزة المتصلة الآن")
    def btn_devices_handler(message):
        if not is_admin(message): return
        show_active_devices(message.chat.id)

    # ------------------
    # COMMANDS IN DETAIL
    # ------------------

    @bot.message_handler(commands=['status'])
    def cmd_status(message):
        if not is_admin(message): return
        show_current_status(message.chat.id)

    @bot.message_handler(commands=['hide_category'])
    def cmd_hide_cat(message):
        if not is_admin(message): return
        args = telebot.util.extract_arguments(message.text)
        if not args:
            bot.reply_to(message, "⚠️ يرجى كتابة اسم الباقة بعد الأمر، مثال:\n`/hide_category رياضة`", parse_mode="Markdown")
            return
        
        with data_lock:
            global config
            existing = config.get("hidden_categories", "")
            cats = [c.strip().lower() for c in existing.split(",") if c.strip()]
            new_cat = args.strip().lower()
            if new_cat not in cats:
                cats.append(new_cat)
            config["hidden_categories"] = ",".join(cats)
            save_config(config)
        
        bot.reply_to(message, f"🚫 تم حجب باقة: *{args}* من التطبيق بنجاح.", parse_mode="Markdown")

    @bot.message_handler(commands=['show_category'])
    def cmd_show_cat(message):
        if not is_admin(message): return
        args = telebot.util.extract_arguments(message.text)
        if not args:
            bot.reply_to(message, "⚠️ يرجى كتابة اسم الباقة بعد الأمر، مثال:\n`/show_category رياضة`", parse_mode="Markdown")
            return
        
        with data_lock:
            global config
            existing = config.get("hidden_categories", "")
            cats = [c.strip().lower() for c in existing.split(",") if c.strip()]
            target_cat = args.strip().lower()
            if target_cat in cats:
                cats.remove(target_cat)
            config["hidden_categories"] = ",".join(cats)
            save_config(config)
        
        bot.reply_to(message, f"✅ تم إعادة إظهار باقة: *{args}* بالتطبيق بنجاح.", parse_mode="Markdown")

    @bot.message_handler(commands=['hide_channel'])
    def cmd_hide_ch(message):
        if not is_admin(message): return
        args = telebot.util.extract_arguments(message.text)
        if not args:
            bot.reply_to(message, "⚠️ يرجى كتابة اسم القناة بعد الأمر، مثال:\n`/hide_channel bein`", parse_mode="Markdown")
            return
        
        with data_lock:
            global config
            existing = config.get("hidden_channels", "")
            chs = [c.strip().lower() for c in existing.split(",") if c.strip()]
            new_ch = args.strip().lower()
            if new_ch not in chs:
                chs.append(new_ch)
            config["hidden_channels"] = ",".join(chs)
            save_config(config)
        
        bot.reply_to(message, f"🚫 تم حجب القناة المحتوية على: *{args}* بنجاح.", parse_mode="Markdown")

    @bot.message_handler(commands=['show_channel'])
    def cmd_show_ch(message):
        if not is_admin(message): return
        args = telebot.util.extract_arguments(message.text)
        if not args:
            bot.reply_to(message, "⚠️ يرجى كتابة اسم القناة بعد الأمر، مثال:\n`/show_channel bein`", parse_mode="Markdown")
            return
        
        with data_lock:
            global config
            existing = config.get("hidden_channels", "")
            chs = [c.strip().lower() for c in existing.split(",") if c.strip()]
            target_ch = args.strip().lower()
            if target_ch in chs:
                chs.remove(target_ch)
            config["hidden_channels"] = ",".join(chs)
            save_config(config)
        
        bot.reply_to(message, f"✅ تم إلغاء حجب القناة المحتوية على: *{args}* بنجاح.", parse_mode="Markdown")

    @bot.message_handler(commands=['hide_tab'])
    def cmd_hide_tab(message):
        if not is_admin(message): return
        args = telebot.util.extract_arguments(message.text)
        if not args:
            bot.reply_to(message, "⚠️ يرجى كتابة معرف التبويب (home, channels, favorites, settings)، مثال:\n`/hide_tab settings`", parse_mode="Markdown")
            return
        
        with data_lock:
            global config
            existing = config.get("hidden_tabs", "")
            tabs = [t.strip().lower() for t in existing.split(",") if t.strip()]
            new_tab = args.strip().lower()
            if new_tab not in tabs:
                tabs.append(new_tab)
            config["hidden_tabs"] = ",".join(tabs)
            save_config(config)
        
        bot.reply_to(message, f"🚫 تم إخفاء التبويب: *{args}* من الشريط السفلي بنجاح.", parse_mode="Markdown")

    @bot.message_handler(commands=['show_tab'])
    def cmd_show_tab(message):
        if not is_admin(message): return
        args = telebot.util.extract_arguments(message.text)
        if not args:
            bot.reply_to(message, "⚠️ يرجى كتابة معرف التبويب (home, channels, favorites, settings)، مثال:\n`/show_tab settings`", parse_mode="Markdown")
            return
        
        with data_lock:
            global config
            existing = config.get("hidden_tabs", "")
            tabs = [t.strip().lower() for t in existing.split(",") if t.strip()]
            target_tab = args.strip().lower()
            if target_tab in tabs:
                tabs.remove(target_tab)
            config["hidden_tabs"] = ",".join(tabs)
            save_config(config)
        
        bot.reply_to(message, f"✅ تم إعادة إظهار التبويب: *{args}* في الشريط السفلي بنجاح.", parse_mode="Markdown")

    @bot.message_handler(commands=['set_announcement'])
    def cmd_announcement(message):
        if not is_admin(message): return
        args = telebot.util.extract_arguments(message.text)
        if not args:
            bot.reply_to(message, "⚠️ يرجى كتابة المدخلات كالتالي:\n`/set_announcement true عنوان_التنبيه نص_الرسالة`", parse_mode="Markdown")
            return
        
        try:
            parts = args.split(" ", 2)
            show = parts[0].strip().lower() == "true"
            title = parts[1].strip() if len(parts) > 1 else "تنبيه"
            msg_content = parts[2].strip() if len(parts) > 2 else ""
            
            with data_lock:
                global config
                config["announcement_show"] = show
                config["announcement_title"] = title
                config["announcement_message"] = msg_content
                save_config(config)
                
            bot.reply_to(message, f"📢 *تم تعديل التنبيه العام بنجاح!*\n\n• الحالة: {'نشط' if show else 'ملغى'}\n• العنوان: {title}\n• الرسالة: {msg_content}", parse_mode="Markdown")
        except Exception as e:
            bot.reply_to(message, f"❌ حدث خطأ أثناء معالجة الأمر: {str(e)}")

    # ------------------
    # HELPER FUNCTIONS
    # ------------------

    def show_current_status(chat_id):
        with data_lock:
            cfg = config.copy()
            telemetry = telemetry_data.copy()
        
        # Calculate active users (pinged within last 5 minutes)
        five_mins_ago = int(time.time()) - 300
        active_count = sum(1 for d in telemetry.values() if d.get("last_seen", 0) >= five_mins_ago)
        watching_count = sum(1 for d in telemetry.values() if d.get("last_seen", 0) >= five_mins_ago and d.get("status") == "watching")
        
        status_msg = (
            "📊 *الحالة الحالية للتطبيق والإعدادات السحابية:*\n\n"
            f"🔌 *حالة البث:* {'🚫 مغلق مؤقتاً (شاشة الصيانة)' if cfg.get('hide_all_channels') else '✅ متاح ويعمل بشكل ممتاز'}\n"
            f"🛠️ *قنوات التطبيق (المدمجة):* {'🚫 مخفية ومغلقة بالكامل' if cfg.get('hide_all_developer_options') else '✅ ظاهرة ونشطة مع إعداداتها'}\n"
            f"📱 *المتصلين الآن (آخر 5 د):* `{active_count}` مستخدم نشط\n"
            f"📺 *يشاهدون البث الآن:* `{watching_count}` مستخدم\n\n"
            f"🚫 *الباقات المحجوبة:* `{cfg.get('hidden_categories') or 'لا يوجد'}`\n"
            f"🚫 *القنوات المحجوبة:* `{cfg.get('hidden_channels') or 'لا يوجد'}`\n"
            f"🚫 *التبويبات المخفية:* `{cfg.get('hidden_tabs') or 'لا يوجد'}`\n\n"
            f"📢 *الإعلان العام:* {'🟢 مفعل' if cfg.get('announcement_show') else '🔴 معطل'}\n"
            f"📍 *عنوان الإعلان:* `{cfg.get('announcement_title')}`\n"
            f"📝 *نص الإعلان:* `{cfg.get('announcement_message') or 'لا يوجد'}`\n\n"
            f"🔗 *رابط الدعم الفني:* `{cfg.get('support_button_url')}`"
        )
        bot.send_message(chat_id, status_msg, parse_mode="Markdown")

    def show_active_devices(chat_id):
        with data_lock:
            telemetry = telemetry_data.copy()
            
        five_mins_ago = int(time.time()) - 300
        active_devices = [
            (uid, d) for uid, d in telemetry.items() 
            if d.get("last_seen", 0) >= five_mins_ago
        ]
        
        if not active_devices:
            bot.send_message(chat_id, "ℹ️ *لا توجد أجهزة نشطة متصلة بالتطبيق حالياً.*", parse_mode="Markdown")
            return
            
        msg = f"📱 *قائمة الأجهزة النشطة حالياً ({len(active_devices)} أجهزة):*\n\n"
        for idx, (uid, d) in enumerate(active_devices, 1):
            status_emoji = "📺 يشاهد" if d.get("status") == "watching" else "🟢 متصل"
            ch_info = f" ({d.get('active_channel')})" if d.get("active_channel") else ""
            msg += (
                f"{idx}. *{d.get('device_model')}* (OS: {d.get('os_version')})\n"
                f"   • الحالة: {status_emoji}{ch_info}\n"
                f"   • الإصدار: {d.get('app_version')} | معرف: `{uid[:8]}...`\n\n"
            )
            
            # Prevent sending massive messages
            if len(msg) > 3000:
                bot.send_message(chat_id, msg, parse_mode="Markdown")
                msg = ""
                
        if msg:
            bot.send_message(chat_id, msg, parse_mode="Markdown")


# -------------------------------------------------------------
# 4. Main Server Startup Threading
# -------------------------------------------------------------

def run_tele_bot():
    if not bot:
        print("Telegram BOT_TOKEN is missing! Bot commands won't be initialized.")
        return
    while True:
        try:
            print("Starting Telegram Bot Polling...")
            bot.infinity_polling(timeout=10, long_polling_timeout=5)
        except Exception as e:
            print(f"Error in bot polling loop: {e}")
            time.sleep(5)

# Start Telegram Bot thread globally so it runs when Gunicorn imports the module
if bot:
    bot_thread = threading.Thread(target=run_tele_bot, daemon=True)
    bot_thread.start()

if __name__ == '__main__':
    # Start Flask Web Server locally if run directly
    port = int(os.environ.get("PORT", 8080))
    app.run(host='0.0.0.0', port=port)
