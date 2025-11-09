import json
import os

dir = os.path.dirname(__file__)
dir_lang = os.path.join(dir, "../")

truth = "en_us.json"

langs = [f for f in os.listdir(dir_lang) if f.endswith(".json") and f != truth]

with open(os.path.join(dir_lang, truth), 'r', encoding="utf8") as truthFile:
    truthJson = json.load(truthFile)
    for lang in langs:
        newLang = {}
        with open(os.path.join(dir_lang, lang), 'r', encoding="utf8") as currentFile:
            langJson = json.load(currentFile)
            for key, truthValue in truthJson.items():
                newLang[key] = truthValue
                if key in langJson:
                    newLang[key] = langJson[key]
                else:
                    print("Key: " + key + " not in lang")
        with open(os.path.join(dir_lang, lang), 'w', encoding="utf8") as currentFile:
            currentFile.write(json.dumps(newLang, ensure_ascii=False, indent="\t"))