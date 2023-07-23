import json
import os

dir = os.path.dirname(__file__)

truth = "en_us.json"
langs = [
    "sv_se.json",
    "uk_ua.json",
    "zh_cn.json",
    "de_de.json",
    "es_es.json",
    "ja_jp.json",
    "ko_kr.json",
    "pt_br.json",
    "ru_ru.json"
]

with open(dir + "/../" + truth, 'r', encoding="utf8") as truthFile:
    truthJson = json.loads(truthFile.read())
    for lang in langs:
        newLang = {}
        with open(dir + "/../" + lang, 'r', encoding="utf8") as currentFile:
            langJson = json.loads(currentFile.read())
            for (key, truthValue) in truthJson.items():
                newLang[key] = truthValue
                if key in langJson:
                    print("Key: " + key + " not in lang")
                    newLang[key] = langJson[key]
                    
        with open(dir + "/../" + lang, 'w', encoding="utf8") as currentFile:
            currentFile.write(json.dumps(newLang, ensure_ascii=False, indent="\t"))

