# Evidence pracovní doby

Původně zpracováno pro účely semestrálního projektu programování v Java. Dále rozpracováno do stádia nasazení aplikace v provozu. Aplikce je nyní využitá pro tvoření evidence pracovní doby městské policie.

## Popis aplikace

Hlavním cílem aplikace bylo propojit evidenci docházky vedenou v programu Excel s jednoduchým a přehledným uživatelským rozhraním. V aplikaci se evidují základní údaje o zaměstnanci, které jsou potřeba pro následné vygenerování Excelové šablony pro zaznamenávání a plánování docházky. Tuto šablonu je poté možné načíst do aplikace a výběrem konkrétního zaměstnance a měsíce přejít k upravení docházky. Aplikace načte automaticky podle výběru potřebné údaje ze šablony a zobrazí je uživateli v editovatelné tabulce. 

V tabulce jsou obsaženy informace o začátku a konci směny, typ směny a v zavislosti na nastavení organizace výpočet odpracovaných a příplatkových hodin. Jeli aplikace připojena k internetu vypočte hodiny odpracované ve svátky. 

## Budoucí možný rozvoj

##### zpřístupnění výpočtů příplatkových hodin pomocí REST
##### rozšíření o modul plánování směn
