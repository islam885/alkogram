"""Read-only static checks for Alkogram media changes; not an Android runtime test.
Run from the repository root: python Tools/check_alkogram_media.py
"""
from collections import Counter
from pathlib import Path
import re
import subprocess
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / "TMessagesProj/src/main/java"
RES = ROOT / "TMessagesProj/src/main/res"

def read(relative):
    return (JAVA / relative).read_text(encoding="utf-8")

config = read("com/exteragram/messenger/ExteraConfig.java")
settings = read("com/exteragram/messenger/preferences/AlkogramPrivacyPreferencesActivity.java")
sending = read("org/telegram/messenger/SendMessagesHelper.java")
chat = read("org/telegram/ui/ChatActivity.java")
cleaner = read("com/exteragram/messenger/utils/AlkogramPhotoPrivacy.java")
locale = read("org/telegram/messenger/LocaleController.java")

for key in ("alko_stripPhotoMetadata", "alko_saveRoundVideo", "formatTimeWithSeconds"):
    assert f'getBoolean("{key}", false)' in config, key
    assert f'putBoolean("{key}"' in settings, key
for row in ("mediaHeaderRow", "stripMetadataRow", "stripMetadataDetailRow",
            "saveRoundVideoRow", "saveRoundVideoDetailRow", "secondsRow"):
    assert settings.count(f"{row} = newRow();") == 1, row
    assert f"position == {row}" in settings, row

# Validate every resource XML, including duplicate strings in changed locales.
xml_files = list(RES.rglob("*.xml"))
for path in xml_files:
    ET.parse(path)
resources = {}
for folder in ("values", "values-ru"):
    tree = ET.parse(RES / folder / "extera.xml")
    names = [node.attrib["name"] for node in tree.getroot() if node.tag == "string"]
    assert not [name for name, count in Counter(names).items() if count > 1], folder
    resources[folder] = set(names)
new_strings = set(re.findall(r"R\.string\.(Alko\w+)", settings + chat + cleaner))
for folder, names in resources.items():
    assert new_strings <= names, (folder, new_strings - names)
assert (RES / "raw/info.json").exists()
assert list(RES.rglob("msg_gallery.*"))

# Local source protection, format gate, full orientation handling, fail-closed paths.
assert "new FileOutputStream(output)" in cleaner
assert "new FileOutputStream(source)" not in cleaner
assert "saveAttributes(" not in cleaner
assert "input.read() != 0xff || input.read() != 0xd8" in cleaner
assert "Bitmap.CompressFormat.JPEG" in cleaner
assert "OutOfMemoryError" in cleaner
for orientation in ("FLIP_HORIZONTAL", "ROTATE_180", "FLIP_VERTICAL", "TRANSPOSE",
                    "ROTATE_90", "TRANSVERSE", "ROTATE_270"):
    assert f"ExifInterface.ORIENTATION_{orientation}" in cleaner
start = sending.index("private static int prepareSendingDocumentInternal(")
end = sending.index("final File f = new File(path);", start)
document_prepare = sending[start:end]
assert "cleanJpeg(path, null)" in document_prepare
assert "originalPath = cleanPath" in document_prepare
assert "return ERROR_TYPE_UNSUPPORTED; // Fail closed" in document_prepare
start = sending.index("public static void prepareSendingMedia(")
end = sending.index("HashMap<SendingMediaInfo, MediaSendPrepareWorker> workers;", start)
album_prepare = sending[start:end]
assert "for (SendingMediaInfo info : media)" in album_prepare
assert "!info.isVideo && info.videoEditedInfo == null && info.searchImage == null" in album_prepare
assert "return; // Never fall back" in album_prepare

# Menu action is wired both ways, and protection checks bypass no flags or network.
assert chat.count("options.add(OPTION_ALKO_SAVE_ROUND_VIDEO)") == 1
assert chat.count("case OPTION_ALKO_SAVE_ROUND_VIDEO:") == 1
start = chat.index("private boolean canSaveAlkogramRoundVideo(")
end = chat.index("private void saveMessageToGallery(", start)
round_code = chat[start:end]
for guard in ("message.messageOwner.noforwards", "currentChat.noforwards", "migrated.noforwards",
              "message.messageOwner.ttl != 0", "ttl_seconds != 0", "isSecretMedia",
              "currentEncryptedChat != null", "!ExteraConfig.alkoSaveRoundVideo"):
    assert guard in round_code, guard
assert "isChatNoForwards(" not in round_code
assert "loadFile(" not in round_code and "sendRequest(" not in round_code
assert "getPathToMessage(" in round_code and "isLoadingFile(" in round_code
assert "WRITE_EXTERNAL_STORAGE" in round_code
assert "MediaController.saveFile(file.toString(), getParentActivity(), 1" in round_code
assert "recreateFormatters();" in settings and "rebuildAllFragmentViews(false, false)" in settings
assert "ExteraConfig.formatTimeWithSeconds" in locale
assert '"HH:mm:ss"' in locale and '"h:mm:ss a"' in locale

# Basic Java lexical delimiter check, not type checking or compilation.
for name, source in (("config", config), ("settings", settings), ("sending", sending),
                     ("chat", chat), ("cleaner", cleaner)):
    tokens = re.sub(r'//[^\n]*|/\*.*?\*/|"(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\'',
                    "", source, flags=re.S)
    stack = []
    pairs = {")": "(", "]": "[", "}": "{"}
    for char in tokens:
        if char in "([{":
            stack.append(char)
        elif char in pairs:
            assert stack and stack.pop() == pairs[char], name
    assert not stack, name

subprocess.run(["git", "diff", "--check"], cwd=ROOT, check=True)
print(f"PASS: {len(xml_files)} XML files, localized strings, prefs/UI wiring, privacy guards, Java delimiters, git diff --check")
print("NOT RUN: Android compilation, JPEG runtime tests and device UI/gallery tests (require JDK/SDK).")
print("BLOCKED: Hide Stories has no Stories UI/protocol implementation in this source version; no inert toggle added.")
