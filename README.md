# ZDRAVNIKI

Desktop aplikacija, ki omogoča tajništvom naročanje svojih delavcev na zdravniške preglede in je razdeljena na tajniški in zdravniški del.  
Zavzema ustvarjanje tajniškega računa in zdravniškega računa.  
Znotraj tajništva je možna kreacija oddelkov v tajništvu, dodajanje delavcev v želen oddelek, izvajanje naročanja na zdravniške preglede in spreminjanje ter brisanje vsega naštetega.  
Na drugi strani omogoča zdravnikom vpogled v vse prihajajoče in pretekle preglede, spreminjanje le-teh in naročanje celotnih oddelkov ali delavcev na pregled.  
Aplikacija je bila ustvarjena v Javi, z vizualnimi elementi in razporeditvijo v Swing. Podatkovno bazo gosti Aiven, strežniški podprogrami so bili napisani preko Datagripa.

## **POSTGRESQL strežniški podprogrami**

### ZDRAVNIKI:

**checkZdravnikSifra** / Preveri, ali vnesena šifra zdravnika obstaja, in če obstaja, vrne njegov naziv, sicer vrne 'FALSE'.

CREATE OR REPLACE FUNCTION checkZdravnikSifra(ssifra VARCHAR)

RETURNS TABLE (iime VARCHAR) AS  
$$  
BEGIN  
IF ssifra IN (SELECT sifra FROM zdravniki) THEN  
RETURN QUERY  
SELECT naziv FROM zdravniki WHERE sifra = ssifra;  
ELSE  
RETURN QUERY SELECT 'FALSE'::VARCHAR;  
END IF;  
END;  
$$  
LANGUAGE plpgsql;

  
**insertajZdravnika** / Vstavi novega zdravnika z nazivom in šifro, če ta šifra še ne obstaja in če patronska šifra obstaja. V nasprotnem primeru vrne ustrezno napako.

CREATE OR REPLACE FUNCTION insertajZdravnika(patronsifra VARCHAR, ssifra VARCHAR, nnaziv VARCHAR)  
RETURNS VARCHAR AS  
$$  
BEGIN  
IF patronsifra IN (SELECT sifra FROM zdravniki) THEN  
IF (ssifra NOT IN (SELECT sifra FROM zdravniki)) THEN  
INSERT INTO zdravniki(sifra, naziv) VALUES(ssifra, nnaziv);  
ELSE  
RETURN 'Šifra že obstaja';  
END IF;  
ELSE  
RETURN 'Patronska šifra ne obstaja.';  
END IF;  
RETURN 'Končalo se je.';  
END;  
$$  
LANGUAGE plpgsql;

  
**updajtajZdravnika** / Posodobi naziv in šifro obstoječega zdravnika, če se ujemata s starimi vrednostmi.

CREATE OR REPLACE FUNCTION updajtajZdravnika(csifra VARCHAR, nsifra VARCHAR, cnaziv VARCHAR, nnaziv VARCHAR)  
RETURNS VOID AS  
$$  
BEGIN  
UPDATE zdravniki  
SET naziv = nnaziv, sifra = nsifra  
WHERE naziv = cnaziv AND sifra = csifra;  
END;  
$$  
LANGUAGE plpgsql;

  
**deletajZdravnika** / Izbriše zdravnika iz baze glede na podano šifro. 

CREATE OR REPLACE FUNCTION deletajZdravnika(csifra VARCHAR)  
RETURNS VOID AS  
$$  
BEGIN  
DELETE FROM zdravniki  
WHERE sifra = csifra;  
END;  
$$  
LANGUAGE plpgsql;

  
### TAJNISTVA

**checkTajnistvoCredentials** / Preveri, ali kombinacija e-pošte in gesla v tabeli tajništva obstaja, ter če obstaja, vrne osnovne podatke tajništva. Če ne obstaja, vrne -1 in dve 'FALSE' vrednosti.

CREATE OR REPLACE FUNCTION checkTajnistvoCredentials(eemail VARCHAR, ppass VARCHAR)  
RETURNS TABLE (iid INT, iime VARCHAR, gglavnia\_tajnikca VARCHAR) AS  
$$  
BEGIN  
IF ppass IN (SELECT pass FROM tajnistva WHERE email = eemail) THEN  
RETURN QUERY  
SELECT id, ime, glavnia\_tajnikca FROM tajnistva WHERE email = eemail AND pass = ppass;  
ELSE  
RETURN QUERY SELECT -1::INT, 'FALSE'::VARCHAR, 'FALSE'::VARCHAR;  
END IF;  
END;  
$$  
LANGUAGE plpgsql;

  
**insertajTajnistvo** / Vstavi novo tajništvo v bazo. Poštno številko pretvori v kraj\_id preko povezave s tabelo kraji.

CREATE OR REPLACE FUNCTION insertajTajnistvo (ttime varchar, temail varchar, ttelefon varchar, tglavnia\_tajnikca varchar, tnaslov varchar, tposta varchar, tpass varchar)  
RETURNS VOID  
AS  
$$  
BEGIN  
INSERT INTO tajnistva (ime, email, telefon, glavnia\_tajnikca, naslov, kraj\_id, pass)  
VALUES (ttime, temail, ttelefon, tglavnia\_tajnikca, tnaslov, (SELECT id FROM kraji WHERE posta = tposta), tpass);  
END;  
$$  
LANGUAGE plpgsql;

  
**updajtajTajnistvo**: Posodobi podatke obstoječega tajništva glede na ID. Vključno s kraj\_id, ki ga poišče glede na vneseno poštno številko.

CREATE OR REPLACE FUNCTION updajtajTajnistvo (tid int, ttime varchar, temail varchar, ttelefon varchar, tglavnia\_tajnikca varchar, tnaslov varchar, tposta varchar, tgeslo varchar )  
RETURNS VOID  
AS  
$$  
BEGIN  
UPDATE tajnistva

SET ime = ttime, email = temail, telefon = ttelefon, glavnia\_tajnikca = tglavnia\_tajnikca, naslov = tnaslov, kraj\_id = (SELECT id FROM kraji WHERE posta = tposta), pass = tgeslo  
WHERE id = tid;  
END;  
$$  
LANGUAGE plpgsql;

  
  
**deletajTajnistvoTrigger: **Preden izbrišeš tajništvo, izbriše vse delavce in oddelke povezane s tem tajništvom. Funkcija se sproži avtomatsko prek sprožilca (triggerja).

CREATE OR REPLACE FUNCTION deletajTajnistvoTrigger()  
RETURNS TRIGGER AS  
$$  
BEGIN  
DELETE FROM delavci  
WHERE oddelek\_id IN (  
SELECT id FROM oddelki  
WHERE tajnistvo\_id = OLD.id  
);

DELETE FROM oddelki  
WHERE tajnistvo\_id = OLD.id;

RETURN OLD;  
END;  
$$ LANGUAGE plpgsql;

CREATE TRIGGER deleteTajnistvoTrigger  
BEFORE DELETE ON tajnistva  
FOR EACH ROW  
EXECUTE FUNCTION deletajTajnistvoTrigger();

  
**deletajTajnistvo**: Izbriše tajništvo glede na ID. Sproži se tudi trigger, ki poskrbi, da se odstranijo povezani podatki.

CREATE OR REPLACE FUNCTION deletajTajnistvo(tid int)  
RETURNS VOID  
AS  
$$  
BEGIN  
DELETE FROM tajnistva  
WHERE id = tid;  
END;  
$$  
LANGUAGE plpgsql;

  
**prikaziTajnistva**: Vrne vse tajništva skupaj s pripadajočimi podatki iz tabele kraji. Vsaka vrstica vsebuje ime, e-pošto, telefon, naslov, vodjo in kraj.

CREATE OR REPLACE FUNCTION prikaziTajnistva ()  
RETURNS TABLE  
(  
iid int,  
iime varchar,  
eemail varchar,  
ttelefon varchar,  
gglavnia\_tajnikca varchar,  
nnaslov varchar,  
kkraj varchar  
)  
AS  
$$  
BEGIN  
RETURN QUERY  
SELECT t.id AS idd, t.ime AS iime, t.email AS eemail, t.telefon AS ttelefon,  
t.glavnia\_tajnikca AS gglavnia\_tajnikca, t.naslov AS nnaslov, k.ime AS kkraj  
FROM tajnistva t INNER JOIN kraji k ON k.id = t.kraj\_id;  
END;  
$$  
LANGUAGE plpgsql;

  
**prikaziTajnistvo**: Vrne eno specifično tajništvo glede na ID, z dodatnim prikazom gesla. 

CREATE OR REPLACE FUNCTION prikaziTajnistvo(tid int)

RETURNS TABLE  
(  
iid int,  
iime varchar,  
eemail varchar,  
ttelefon varchar,  
gglavnia\_tajnikca varchar,  
nnaslov varchar,  
kkraj varchar,  
ppass varchar  
)  
AS  
$$  
BEGIN  
RETURN QUERY  
SELECT t.id AS idd, t.ime AS iime, t.email AS eemail, t.telefon AS ttelefon,  
t.glavnia\_tajnikca AS gglavnia\_tajnikca, t.naslov AS nnaslov, k.ime AS kkraj, t.pass AS ppass  
FROM tajnistva t INNER JOIN kraji k ON k.id = t.kraj\_id  
WHERE t.id = tid;  
END;  
$$  
LANGUAGE plpgsql;

  
### KRAJI

**insertajKraj** / Funkcija vstavi nov kraj v tabelo kraji, tako da ji podamo ime in poštno številko. 

CREATE OR REPLACE FUNCTION insertajKraj (kime varchar, kposta varchar)  
RETURNS VOID  
AS  
$$  
BEGIN  
INSERT INTO kraji (ime, posta)  
VALUES (kime, kposta);  
END;  
$$  
LANGUAGE plpgsql;

  
**updajtajKraj** / Funkcija posodobi ime in poštno številko kraja, ki ima dano začetno poštno številko. Uporablja se za spremembo obstoječih podatkov v tabeli kraji.

CREATE OR REPLACE FUNCTION updajtajKraj (ckposta varchar, nkrajime varchar, nkrajposta varchar)  
RETURNS VOID  
AS  
$$  
BEGIN  
UPDATE kraji  
SET ime = nkrajime, posta = nkrajposta  
WHERE posta = ckposta;  
END;  
$$  
LANGUAGE plpgsql;

  
**deletajKrajTrigger** / Funkcija znotraj sprožilca (triggerja), ki ob brisanju kraja posodobi vse povezane vrstice v tajnistva na nadomestni kraj s poštno številko ‘0001’. Tako se prepreči prekinitvam povezav med tabelami.

CREATE OR REPLACE FUNCTION deletajKrajTrigger ()  
RETURNS TRIGGER  
AS  
$$  
BEGIN  
UPDATE tajnistva  
SET kraj\_id = (SELECT id FROM kraji WHERE posta = '0001')  
WHERE kraj\_id = (SELECT id FROM kraji WHERE id = OLD.id);  
RETURN OLD;  
END;  
$$  
LANGUAGE plpgsql;

  
**deletajKraj** / Funkcija izbriše kraj iz tabele kraji na podlagi poštne številke. Če obstaja sprožilec, se ob tem izvede tudi posodobitev povezanih tabel.

CREATE OR REPLACE FUNCTION deletajKraj (kposta varchar)

RETURNS VOID  
AS  
$$  
BEGIN  
DELETE FROM kraji  
WHERE posta = kposta;  
END;  
$$  
LANGUAGE plpgsql;

  
**prikaziKraje** / Funkcija vrne vse vrstice iz tabele kraji, vključno z ID-jem, imenom in poštno številko. Namenjena je prikazu vseh obstoječih krajev.

CREATE OR REPLACE FUNCTION prikaziKraje ()  
RETURNS TABLE  
(  
iid int,  
iime varchar,  
pposta varchar  
)  
AS  
$$  
BEGIN  
RETURN QUERY  
SELECT \* FROM kraji;  
END;  
$$  
LANGUAGE plpgsql;

  
**prikaziKraj** / Funkcija poišče kraj glede na poštno številko in vrne njegove podatke. Rezultat vsebuje ID, ime in poštno številko izbrane vrstice.

CREATE OR REPLACE FUNCTION prikaziKraj (kposta varchar)  
RETURNS TABLE  
(  
iid int,  
iime varchar,  
pposta varchar  
)  
AS  
$$  
BEGIN  
RETURN QUERY  
SELECT \* FROM kraji  
WHERE posta = kposta;  
END;  
$$  
LANGUAGE plpgsql;

  
### ODDELKI

  
**insertajOddelek** / Funkcija vstavi nov oddelek z imenom, opisom in imenom tajništva, ki mu pripada. Tajnistvo se določi prek poizvedbe po njegovem imenu.

CREATE OR REPLACE FUNCTION insertajOddelek(oime varchar, oopis varchar, tiime varchar)  
RETURNS VOID  
AS  
$$  
BEGIN  
INSERT INTO oddelki (ime, opis, tajnistvo\_id)  
VALUES (oime, oopis, (SELECT id FROM tajnistva WHERE ime = tiime));  
END;  
$$  
LANGUAGE plpgsql;

  
**updajtajOddelek** / Funkcija posodobi ime, opis in pripadajoče tajništvo oddelka glede na njegov ID. Tajnistvo se znova določi preko imena.

CREATE OR REPLACE FUNCTION updajtajOddelek(oid int, oime varchar, oopis varchar, tiime varchar)  
RETURNS VOID  
AS  
$$  
BEGIN  
UPDATE oddelki  
SET ime = oime, opis = oopis, tajnistvo\_id = (SELECT id FROM tajnistva WHERE ime = tiime)  
WHERE id = oid;  
END;  
$$  
LANGUAGE plpgsql;

  
**deletajOddelekTrigger** / Funkcija v sprožilcu izbriše vse delavce, ki pripadajo oddelku, tik preden je ta izbrisan. Tako se ohrani podatkovna integriteta brez “osirotelih” delavce

CREATE OR REPLACE FUNCTION deletajOddelekTrigger()  
RETURNS TRIGGER AS  
$$  
BEGIN  
DELETE FROM delavci  
WHERE oddelek\_id = OLD.id;  
RETURN OLD;  
END;  
$$ LANGUAGE plpgsql;

  
**deletajOddelek** / Funkcija izbriše obstoječi oddelek iz baze na podlagi njegovega ID-ja. 

CREATE OR REPLACE FUNCTION deletajOddelek(oid int)  
RETURNS VOID  
AS  
$$  
BEGIN  
DELETE FROM oddelki  
WHERE id = oid;  
END;  
$$  
LANGUAGE plpgsql;

  
**prikaziOddelke** / Funkcija vrne seznam vseh oddelkov, vključno z njihovim opisom in imenom tajništva. Združuje podatke iz tabele oddelki in tajnistva.

CREATE OR REPLACE FUNCTION prikaziOddelke()  
RETURNS TABLE  
(  
iid int,  
iime varchar,  
oopis text,  
ttajnistvo varchar  
)  
AS  
$$  
BEGIN  
RETURN QUERY  
SELECT o.id AS idd, o.ime AS iime, o.opis AS opis, t.ime AS ttajnistvo  
FROM tajnistva t INNER JOIN oddelki o ON t.id = o.tajnistvo\_id;  
END;  
$$  
LANGUAGE plpgsql;

  
**prikaziOddelek** / Funkcija prikaže en specifičen oddelek na podlagi njegovega ID-ja. Vrne tudi ime pripadajočega tajništva.

CREATE OR REPLACE FUNCTION prikaziOddelek(oid int)  
RETURNS TABLE  
(  
iid int,  
iime varchar,  
oopis text,  
ttajnistvo varchar  
)  
AS  
$$  
BEGIN  
RETURN QUERY  
SELECT o.id AS idd, o.ime AS iime, o.opis AS opis, t.ime AS ttajnistvo  
FROM tajnistva t INNER JOIN oddelki o ON t.id = o.tajnistvo\_id  
WHERE o.id = oid;  
END;  
$$  
LANGUAGE plpgsql;

**  
**

**prikaziOddelkeVTajnistvu** / Funkcija prikaže vse oddelke, ki pripadajo določenemu tajništvu (glede na ID). 

CREATE OR REPLACE FUNCTION prikaziOddelkeVTajnistvu(tid int)  
RETURNS TABLE  
(  
iid int,  
iime varchar,  
oopis text,  
ttajnistvo varchar  
)  
AS  
$$  
BEGIN  
RETURN QUERY  
SELECT o.id AS idd, o.ime AS iime, o.opis AS opis, t.ime AS ttajnistvo  
FROM tajnistva t INNER JOIN oddelki o ON t.id = o.tajnistvo\_id  
WHERE t.id = tid;  
END;  
$$  
LANGUAGE plpgsql;

  
**oddelekcelpregled** / Za vse delavce določenega oddelka ustvari nov pregled s podanimi podatki. / Funkcija pridobi vse EMSO številke delavcev iz oddelka oid in za vsakega posamezno pokliče funkcijo insertajPregled z letnico pregleda, datumom in opombo.

CREATE OR REPLACE FUNCTION oddelekcelpregled(oid INTEGER, pidleto VARCHAR, pdatum TIMESTAMP, popombe TEXT)

RETURNS VOID AS

$$

DECLARE

demso TEXT;

BEGIN

FOR demso IN

SELECT emso FROM delavci WHERE oddelek\_id = oid

LOOP

PERFORM insertajPregled(pidleto, pdatum, popombe, demso);

END LOOP;

END;

$$

LANGUAGE plpgsql

  
### DELAVCI

**insertajDelavca** / Vstavi novega delavca v tabelo delavci glede na podani oddelek (ime). 

CREATE OR REPLACE FUNCTION insertajDelavca (dime varchar, dpriimek varchar, demso varchar, dtelefon varchar, dslikica text, doddelek varchar )  
RETURNS VOID  
AS  
$$  
BEGIN  
INSERT INTO delavci (ime, priimek, emso, telefon, slikica, oddelek\_id)  
VALUES (dime, dpriimek, demso, dtelefon, dslikica, (SELECT id FROM oddelki WHERE ime = doddelek));  
END;  
$$  
LANGUAGE plpgsql;

  
**updajtajDelavca** / Posodobi obstoječega delavca glede na emso; spremeni lahko ime, priimek, telefon, sliko, in povezan oddelek.

CREATE OR REPLACE FUNCTION updajtajDelavca (cdemso varchar, ndemso varchar, ndime varchar, ndpriimek varchar, ndtelefon varchar, ndslikica text, doddelek varchar)  
RETURNS VOID  
AS  
$$  
BEGIN  
UPDATE delavci  
SET ime = ndime, priimek = ndpriimek, emso = ndemso, telefon = ndtelefon, slikica = ndslikica, oddelek\_id = (SELECT id FROM oddelki WHERE ime = doddelek)  
WHERE emso = cdemso;  
END;  
$$  
LANGUAGE plpgsql;

  
**log\_pregledi\_before\_delavec\_delete** / Pred izbrisom delavca shrani povezane preglede v arhivsko tabelo pregledideleted in izbriše te preglede iz pregledi. Uporablja se v sprožilcu (triggerju), ki deluje pred brisanjem.

CREATE OR REPLACE FUNCTION log\_pregledi\_before\_delavec\_delete()  
RETURNS TRIGGER AS  
$$  
DECLARE  
idd varchar;  
ddatum timestamp;  
oopombe text;  
eemso varchar;  
iime varchar;  
pprimek varchar;  
BEGIN  
FOR idd, ddatum, oopombe, eemso, iime, pprimek IN  
SELECT p.id, p.datum, p.opombe, d.emso, d.ime, d.priimek  
FROM pregledi p  
INNER JOIN delavci d ON d.id = p.delavec\_id  
WHERE d.id = OLD.id  
LOOP  
INSERT INTO pregledideleted(id, datum, opombe, emso\_delavca, ime, priimek)  
VALUES (idd, ddatum, oopombe, eemso, iime, pprimek);  
END LOOP;

DELETE FROM pregledi  
WHERE delavec\_id = OLD.id;

RETURN OLD;  
END;  
$$ LANGUAGE plpgsql;

  
**log\_changes\_to\_logdelavci() / **prožilna funkcija (trigger ), ki beleži vse spremembe (vstavljanja, posodobitve, izbrise) v tabeli delavci v namensko dnevniško tabelo logdelavci.

CREATE OR REPLACE FUNCTION log\_changes\_to\_logdelavci()  
RETURNS TRIGGER AS $$  
BEGIN  
IF TG\_OP = 'INSERT' THEN  
INSERT INTO logdelavci (  
id, ime, priimek, emso, telefon, slikica, oddelek\_id,  
datum\_spremembe, tip\_spremembe  
)  
VALUES (  
NEW.id, NEW.ime, NEW.priimek, NEW.emso, NEW.telefon, NEW.slikica,  
NEW.oddelek\_id, now(), 'INSERT'  
);  
RETURN NEW;

ELSIF TG\_OP = 'UPDATE' THEN  
INSERT INTO logdelavci (  
id, ime, priimek, emso, telefon, slikica, oddelek\_id,  
datum\_spremembe, tip\_spremembe  
)  
VALUES (  
NEW.id, NEW.ime, NEW.priimek, NEW.emso, NEW.telefon, NEW.slikica,  
NEW.oddelek\_id, now(), 'UPDATE'  
);  
RETURN NEW;

ELSIF TG\_OP = 'DELETE' THEN  
INSERT INTO logdelavci (  
id, ime, priimek, emso, telefon, slikica, oddelek\_id,  
datum\_spremembe, tip\_spremembe  
)  
VALUES (  
OLD.id, OLD.ime, OLD.priimek, OLD.emso, OLD.telefon, OLD.slikica,  
OLD.oddelek\_id, now(), 'DELETE'  
);  
RETURN OLD;  
END IF;

RETURN NULL;  
END;  
$$ LANGUAGE plpgsql;

  
**deletajDelavca** / Izbriše delavca iz tabele delavci glede na njegov emso.

CREATE OR REPLACE FUNCTION prikaziDelavce()  
RETURNS TABLE  
(  
iid int,  
iime varchar,  
ppriimek varchar,  
eemso varchar,  
ttelefon varchar,  
sslikica text,  
ooddelek varchar,  
ttajnistvo varchar,  
kkraj varchar  
)  
AS  
$$  
BEGIN  
RETURN QUERY  
SELECT d.id AS iid, d.ime AS iime, d.priimek AS pprimek, d.emso AS eemso, d.telefon AS ttelefon, d.slikica AS sslikica,  
o.ime AS ooddelek, t.ime AS ttajnistvo, k.ime AS kkraj  
FROM kraji k INNER JOIN tajnistva t on k.id = t.kraj\_id INNER JOIN oddelki o ON t.id = o.tajnistvo\_id INNER JOIN delavci d on o.id = d.oddelek\_id;  
END;  
$$  
LANGUAGE plpgsql;

  
**prikaziDelavce** / Prikaže vse delavce skupaj z njihovimi pripadajočimi oddelki, tajništvi in kraji. Vrnjen je tabelaričen rezultat z več informacijami.

CREATE OR REPLACE FUNCTION prikaziDelavce()  
RETURNS TABLE  
(  
iid int,  
iime varchar,  
ppriimek varchar,  
eemso varchar,  
ttelefon varchar,  
sslikica text,  
ooddelek varchar,  
ttajnistvo varchar,  
kkraj varchar  
)  
AS  
$$  
BEGIN  
RETURN QUERY  
SELECT d.id AS iid, d.ime AS iime, d.priimek AS pprimek, d.emso AS eemso, d.telefon AS ttelefon, d.slikica AS sslikica,  
o.ime AS ooddelek, t.ime AS ttajnistvo, k.ime AS kkraj  
FROM kraji k INNER JOIN tajnistva t on k.id = t.kraj\_id INNER JOIN oddelki o ON t.id = o.tajnistvo\_id INNER JOIN delavci d on o.id = d.oddelek\_id;  
END;  
$$  
LANGUAGE plpgsql;

  
**prikaziDelavca** / Prikaže enega samega delavca glede na emso, skupaj z vsemi pripadajočimi informacijami iz povezanih tabel.

CREATE OR REPLACE FUNCTION prikaziDelavca(demso varchar)  
RETURNS TABLE  
(  
iid int,  
iime varchar,  
ppriimek varchar,  
eemso varchar,  
ttelefon varchar,  
sslikica text,  
ooddelek varchar,  
ttajnistvo varchar,  
kkraj varchar  
)  
AS  
$$  
BEGIN  
RETURN QUERY  
SELECT d.id AS iid, d.ime AS iime, d.priimek AS pprimek, d.emso AS eemso, d.telefon AS ttelefon, d.slikica AS sslikica,  
o.ime AS ooddelek, t.ime AS ttajnistvo, k.ime AS kkraj  
FROM kraji k INNER JOIN tajnistva t on k.id = t.kraj\_id INNER JOIN oddelki o ON t.id = o.tajnistvo\_id INNER JOIN delavci d on o.id = d.oddelek\_id  
WHERE d.emso = demso;  
END;  
$$  
LANGUAGE plpgsql;

  
**prikaziDelavceVoddelku** / Prikaže vse delavce znotraj določenega oddelka na podlagi id oddelka. Tudi tukaj se prikažejo vse povezane informacije o tajništvu in kraju.

CREATE OR REPLACE FUNCTION prikaziDelavceVoddelku(oid int)  
RETURNS TABLE  
(  
iid int,  
iime varchar,  
ppriimek varchar,  
eemso varchar,  
ttelefon varchar,  
sslikica text,  
ooddelek varchar,  
ttajnistvo varchar,  
kkraj varchar  
)  
AS  
$$  
BEGIN  
RETURN QUERY  
SELECT d.id AS iid, d.ime AS iime, d.priimek AS pprimek, d.emso AS eemso, d.telefon AS ttelefon, d.slikica AS sslikica,  
o.ime AS ooddelek, t.ime AS ttajnistvo, k.ime AS kkraj  
FROM kraji k INNER JOIN tajnistva t on k.id = t.kraj\_id INNER JOIN oddelki o ON t.id = o.tajnistvo\_id INNER JOIN delavci d on o.id = d.oddelek\_id  
WHERE o.id = oid;  
END;  
$$  
LANGUAGE plpgsql;

  
### PREGLEDI

  
**insertajPregled** / Vstavi nov pregled z edinstvenim ID-jem v tabelo pregledi. ID se ustvari naključno in je sestavljen iz leta ter 13 znakov.

CREATE OR REPLACE FUNCTION insertajPregled(pidleto VARCHAR, pdatum TIMESTAMP, popombe TEXT, demso VARCHAR)  
RETURNS VOID AS  
$$  
DECLARE  
new\_id VARCHAR(17);  
charset TEXT := 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';  
rand\_part TEXT := '';  
i INT;  
BEGIN  
LOOP  
rand\_part := '';  
FOR i IN 1..13 LOOP  
rand\_part := rand\_part || substr(charset, floor(random() \* length(charset) + 1)::int, 1);  
END LOOP;  
new\_id := pidleto || rand\_part;  
EXIT WHEN NOT EXISTS (  
SELECT 1 FROM pregledi WHERE id = new\_id  
) AND NOT EXISTS (  
SELECT 1 FROM pregledideleted WHERE id = new\_id  
);  
END LOOP;  
INSERT INTO pregledi (id, datum, opombe, delavec\_id)  
VALUES (new\_id, pdatum, popombe, (SELECT id FROM delavci WHERE emso = demso));  
END;  
$$ LANGUAGE plpgsql;

  
**updajtajPregled** / Posodobi obstoječi pregled v tabeli pregledi glede na njegov ID. Posodobijo se podatki o ID-ju, datumu, opombah in pripadajočem delavcu.

CREATE OR REPLACE FUNCTION updajtajPregled(pid varchar, npid varchar, pdatum timestamp, popombe text, demso varchar)  
RETURNS VOID  
AS  
$$  
BEGIN  
UPDATE pregledi  
SET id = npid, datum = pdatum, opombe = popombe, delavec\_id = (SELECT id FROM delavci WHERE emso = demso)  
WHERE id = pid;  
END;  
$$ LANGUAGE plpgsql;

  
### 

**updajtajPregledDeleted** / Posodobi pregled v arhivski tabeli pregledideleted. Dodaš ali spremeniš osnovne podatke ter osebne podatke delavca.

CREATE OR REPLACE FUNCTION updajtajPregledDeleted(pid varchar, npid varchar, pdatum timestamp, popombe text, demso varchar, dime varchar, dpriimek varchar)  
RETURNS VOID  
AS  
$$  
BEGIN  
UPDATE pregledideleted  
SET id = npid, datum = pdatum, opombe = popombe, emso\_delavca = demso, ime = dime, priimek = dpriimek  
WHERE id = pid;  
END;  
$$ LANGUAGE plpgsql;

  
**deletajPregled** / Izbriše pregled iz pregledi ali pregledideleted, če ta obstaja. Preveri, v kateri tabeli je ID in ga odstrani.

CREATE OR REPLACE FUNCTION deletajPregled(pid varchar)  
RETURNS VOID  
AS  
$$  
BEGIN  
IF(pid IN (SELECT id FROM pregledi)) THEN  
DELETE FROM pregledi WHERE id = pid;  
ELSE IF (pid IN (SELECT id FROM pregledideleted)) THEN  
DELETE FROM pregledideleted WHERE id = pid;  
END IF;  
END IF;  
END;  
$$ LANGUAGE plpgsql;

  
**prikaziPreglede** / Prikaže vse aktivne preglede z osnovnimi podatki in EMŠO pripadajočega delavca. Združuje tabeli pregledi in delavci.

CREATE OR REPLACE FUNCTION prikaziPreglede()  
RETURNS TABLE (iid varchar, ddatum timestamp, oopombe text, eemso varchar)  
AS  
$$  
BEGIN  
RETURN QUERY  
SELECT p.id AS idd, p.datum AS ddatum, p.opombe AS oopombe, d.emso AS eemso  
FROM pregledi p INNER JOIN delavci d ON d.id = p.delavec\_id;  
END;  
$$ LANGUAGE plpgsql;

  
**prikaziPregledeDeleted** / Prikaže vse izbrisane preglede s podatki o delavcu. Rezultati prihajajo iz arhivske tabele pregledideleted.

CREATE OR REPLACE FUNCTION prikaziPregledeDeleted()  
RETURNS TABLE (iid varchar, ddatum timestamp, oopombe text, eemso varchar, iime varchar, pprimek varchar)  
AS  
$$  
BEGIN  
RETURN QUERY  
SELECT \* FROM pregledideleted;  
END;  
$$ LANGUAGE plpgsql;

  
**prikaziPregled** / Prikaže določen pregled iz pregledi glede na njegov ID. Vrne osnovne podatke in EMŠO delavca.

CREATE OR REPLACE FUNCTION prikaziPregled(pid varchar)  
RETURNS TABLE (iid varchar, ddatum timestamp, oopombe text, eemso varchar)  
AS  
$$  
BEGIN  
RETURN QUERY  
SELECT p.id AS idd, p.datum AS ddatum, p.opombe AS oopombe, d.emso AS eemso  
FROM pregledi p INNER JOIN delavci d ON d.id = p.delavec\_id  
WHERE p.id = pid;  
END;  
$$ LANGUAGE plpgsql;

  
**prikaziPregledDeleted** / Prikaže določen izbrisani pregled iz pregledideleted. Vrne tudi ime in priimek delavca.

CREATE OR REPLACE FUNCTION prikaziPregledDeleted(pid varchar)  
RETURNS TABLE (iid varchar, ddatum timestamp, oopombe text, eemso varchar, iime varchar, pprimek varchar)  
AS  
$$  
BEGIN  
RETURN QUERY  
SELECT \* FROM pregledideleted WHERE id = pid;  
END;  
$$ LANGUAGE plpgsql;

  
**prikaziPregledDelavca** / Prikaže vse preglede, ki jih je opravil določen delavec (glede na EMŠO). Združi pregledi in delavci.

CREATE OR REPLACE FUNCTION prikaziPregledDelavca(demso varchar)  
RETURNS TABLE (iid varchar, ddatum timestamp, oopombe text, eemso varchar)  
AS  
$$  
BEGIN  
RETURN QUERY  
SELECT p.id AS idd, p.datum AS ddatum, p.opombe AS oopombe, d.emso AS eemso  
FROM pregledi p INNER JOIN delavci d ON d.id = p.delavec\_id  
WHERE d.emso = demso;  
END;  
$$ LANGUAGE plpgsql;

  
**prikaziPregledDeletedDelavca** / Prikaže vse izbrisane preglede določenega delavca. Iskanje temelji na EMŠO.

CREATE OR REPLACE FUNCTION prikaziPregledDeletedDelavca(demso varchar)  
RETURNS TABLE (iid varchar, ddatum timestamp, oopombe text, eemso varchar, iime varchar, pprimek varchar)  
AS  
$$  
BEGIN  
RETURN QUERY  
SELECT \* FROM pregledideleted WHERE emso\_delavca = demso;  
END;  
$$ LANGUAGE plpgsql;

  
**prikaziPregledOddelka** / Prikaže preglede, na katere so prijavljeni delavci določenega oddelka. Združuje pregledi, delavci in oddelki.

CREATE OR REPLACE FUNCTION prikaziPregledOddelka(oid int)  
RETURNS TABLE (iid varchar, ddatum timestamp, oopombe text, eemso varchar)  
AS  
$$  
BEGIN  
RETURN QUERY  
SELECT p.id AS idd, p.datum AS ddatum, p.opombe AS oopombe, d.emso AS eemso  
FROM pregledi p INNER JOIN delavci d ON d.id = p.delavec\_id  
INNER JOIN oddelki o ON o.id = d.oddelek\_id  
WHERE o.id = oid;  
END;  
$$ LANGUAGE plpgsql;

  
**prikaziPregledTajnistva** / Prikaže preglede znotraj določenega tajništva. Funkcija povezuje pregledi, delavci, oddelki in tajnistva.

CREATE OR REPLACE FUNCTION prikaziPregledTajnistva(tid int)  
RETURNS TABLE (iid varchar, ddatum timestamp, oopombe text, eemso varchar)  
AS  
$$  
BEGIN  
RETURN QUERY  
SELECT p.id AS idd, p.datum AS ddatum, p.opombe AS oopombe, d.emso AS eemso  
FROM pregledi p INNER JOIN delavci d ON d.id = p.delavec\_id  
INNER JOIN oddelki o ON o.id = d.oddelek\_id  
INNER JOIN tajnistva t ON t.id = o.tajnistvo\_id  
WHERE t.id = tid;  
END;  
$$ LANGUAGE plpgsql;

  
  
  
###   

  
  
  
  
  
  
  
  
  
### **.** 