conn = new Mongo();
// Nos vamos a la base de datos ATM,
// si no lo haría por defecto en la BD definida en el docker-compose.yml MONGO_INITDB_DATABASE
db = conn.getDB("ATM");
// Borramos todas las colecciones
db.collection.drop();

db.users.insert({"email":"admin@admin.org","pin":"1234","cash":999999});
db.users.insert({"email":"joseluisgs@users.org","pin":"2222","cash":6000});
db.users.insert({"email":"dylanhurtado@users.org","pin":"5432","cash":9988});
db.users.insert({"email":"unicorniofeliz97@users.org","pin":"1997","cash":3000});
db.users.insert({ "email":"viejosabroso88@users.org","pin":"1988","cash":7500});