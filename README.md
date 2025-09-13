

# SEP-Drive

**SEP-Drive** ist eine webbasierte Mitfahr-App, die im Rahmen des Software Systems Engineering (SoSe 2025, Prof. Dr. Klaus Pohl) entwickelt wurde. Ziel des Projekts ist es, eine Plattform zu bieten, auf der Kunden Fahrten anfragen und Fahrer diese durchführen können. Das System umfasst ein Frontend und ein Backend, die jeweils in separaten Docker-Containern laufen, sowie eine kartenbasierte Visualisierung von Routen.

---

## 📌 Hauptfunktionen

### Benutzerverwaltung

* **Registrierung & Login:** Benutzer können sich als Kunde oder Fahrer registrieren. Die Anmeldung erfolgt über Benutzername, Passwort und Zwei-Faktor-Authentifizierung via E-Mail.
* **Benutzerprofile:**

  * Kundenprofil: Benutzername, Rolle, Name, E-Mail, Geburtsdatum, Profilbild, Rating, Gesamtzahl der Fahrten.
  * Fahrerprofil: Zusätzlich Auto-Klasse (Klein, Medium, Deluxe) und gesammelte Statistiken.

### Fahrten & Routen

* **Fahranfragen:** Kunden geben Startpunkt, Ziel und Fahrzeugklasse an. Unterstützung für aktuelle Position, Adresseingabe, Points of Interest oder Koordinaten.
* **Routenplanung:** Automatische Berechnung von Strecke, Dauer und Preis. Anpassbar für mehrere Zwischenstopps.
* **Kartenvisualisierung:** Interaktive Karte zeigt Start, Ziel, Zwischenstopps und die geplante Route. Zoom- und Verschiebefunktionen verfügbar.

### Zahlungs- und Geldkonten

* Kunden und Fahrer haben eigene Konten in Euro.
* Fahrtkosten werden automatisch vom Kundenkonto abgebucht und dem Fahrer gutgeschrieben.

### Fahrthistorie & Statistiken

* Anzeige aller bisherigen Fahrten mit relevanten Informationen wie Distanz, Dauer, Preis und Bewertungen.
* Fahrer können ihre Leistung über Diagramme analysieren (Einnahmen, Distanz, Bewertung).

### Echtzeit-Interaktion

* **Chat-Funktion:** Fahrer und Kunden können Nachrichten in Echtzeit austauschen.
* **Fahrt-Simulation:** Fortschritt einer Fahrt wird live auf der Karte dargestellt; Geschwindigkeit und Zwischenstopps anpassbar.

### Fahrer Leaderboard

* Übersicht über alle Fahrer mit Statistiken wie gefahrene Distanz, Bewertungen, Gesamtfahrten und verdientes Geld.
* Sortier- und Filtermöglichkeiten nach Spalten und Namen.

---

## 🛠️ Technologien

* **Backend:** Java, Spring Boot
* **Frontend:** Angular
* **Datenbank:** PostgreSQL, Docker-Container
* **Routenplanung:** OpenRouteService API, GPX-Dateien
* **Containerisierung:** Docker & Docker Compose

---

## 🚀 Installation & Start

1. Stelle sicher, dass **Docker** und **Docker Compose** installiert sind.
2. Im Projektverzeichnis ausführen:

```bash
docker-compose up --build
```

3. Die Anwendung ist unter `http://localhost:8080` verfügbar.

---

## 🧪 Tests

* Einheitstests für:

  * Fahrpreisberechnung
  * Auszahlungsvorgänge
  * Fahrer-Statistiken und Leaderboard
* Datenbank lokal angelegt, um Abhängigkeiten von Online-Diensten zu vermeiden.

---

## 🤝 Mitwirken

Beiträge sind willkommen! Bitte Issues erstellen oder Pull Requests senden.

---



