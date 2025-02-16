# BooksRentalSystem

## Descrierea Proiectului
Books Rental API este o aplicație REST pentru gestionarea împrumuturilor de cărți într-o bibliotecă. Aplicația permite administrarea utilizatorilor, a cărților și a împrumuturilor, oferind funcționalități pentru angajați și administratori. Baza de date utilizată este PostgreSQL, iar testele sunt implementate folosind Mockito.

## 10 Business Requirements
1. Se pot adăuga, modifica sau șterge utilizatori.
2. Utilizatorii pot fi clienți, angajați sau administratori.
3. Se pot împrumuta cărți pe o perioadă prestabilită de 14 zile.
4. Angajații înregistrează împrumuturile.
5. Detaliile unui împrumut pot fi editate.
6. Angajații înregistrează returnările cărților.
7. Se poate prelungi termenul de predare cu încă 7 zile.
8. Se poate vizualiza raportul împrumuturilor unui client.
9. Se pot adăuga, modifica sau șterge cărți.
10. Cărțile pot face parte dintr-o categorie.

## 5 Funcționalități MVP
### 1. Gestionare Împrumuturi
- Crearea și modificarea împrumuturilor
- Extinderea termenului de predare
- Vizualizarea raportului împrumuturilor pentru fiecare client

### 2. Gestionare Returnări
- Înregistrarea unei returnări
- Actualizarea statusului de disponibilitate pentru carte

### 3. Gestionare Cărți
- Adăugarea, modificarea și ștergerea cărților
- Listarea cărților
- Filtrarea cărților după titlu, autor sau categorie

### 4. Gestionare Utilizatori
- Adăugarea, modificarea și ștergerea utilizatorilor
- Listarea utilizatorilor

### 5. Gestionare Categorii
- Adăugarea, modificarea și ștergerea categoriilor
- Listarea categoriilor

## Principalele Endpoint-uri REST

### 1. Gestionare Împrumuturi
- `POST /rentals` - Creare împrumut
- `PUT /rentals/{id}` - Modificare împrumut (extinderea unui împrumut)
- `GET /rentals/client/{clientId}` - Raport împrumuturi client

### 2. Gestionare Returnări
- `POST /returns` - Înregistrare returnare

### 3. Gestionare Cărți
- `POST /books` - Adăugare carte
- `PUT /books/{id}` - Modificare carte
- `DELETE /books/{id}` - Ștergere carte
- `GET /books/all` - Listare cărți
- `GET /books/category/{id}` - Filtrare după categorie
- `GET /books/filter/title/{title}` - Filtrare după titlu
- `GET /books/filter/author/{author}` - Filtrare după autor

### 4. Gestionare Utilizatori
- `POST /users` - Adăugare utilizator
- `PUT /users/{id}` - Modificare utilizator
- `DELETE /users/{id}` - Ștergere utilizator
- `GET /users/all` - Listare utilizatori
- `GET /users/{id}` - Afisarea unui utilizator

### 5. Gestionare Categorii
- `POST /categories` - Adăugare categorie
- `PUT /categories/{id}` - Modificare categorie
- `DELETE /categories/{id}` - Ștergere categorie
- `GET /categories/all` - Listare categorii
- `GET /categories/{id}` - Afisarea unei categorii

## Arhitectura Backend
Aplicația este construită pe baza unei arhitecturi tipice Spring Boot, cu următoarele componente principale:

- **Controller Layer**: expune endpoint-urile REST, iar cu ajutorul claselor de tip DTO și Mapper trimite datele către servicii
- **Service Layer**: conține logica de business
- **Repository Layer**: interacționează cu baza de date PostgreSQL
- **Entity Layer**: definește entitățile JPA

## Validări și Testare
- Aplicația include validări pentru câmpurile obligatorii utilizând anotările Spring Validation.
- Testele unitare sunt implementate folosind Mockito pentru a asigura corectitudinea logicii de business.

## Documentație Swagger
- Documentația Swagger poate fi accesată la http://localhost:8080/swagger-ui/index.html

