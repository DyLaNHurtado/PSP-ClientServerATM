conn = new Mongo();
// Nos vamos a la base de datos ATM,
// si no lo haría por defecto en la BD definida en el docker-compose.yml MONGO_INITDB_DATABASE
db = conn.getDB("ATM");
// Borramos todas las colecciones
db.collection.drop();

db.users.insert({"email":"admin@admin.org","pin":"1234","balance":999999});
db.users.insert({"email":"joseluisgs@users.org","pin":"2222","balance":6000});
db.users.insert({"email":"dylanhurtado@users.org","pin":"5432","balance":9988});
db.users.insert({"email":"unicorniofeliz97@users.org","pin":"1997","balance":3000});
db.users.insert({ "email":"viejosabroso88@users.org","pin":"1988","balance":7500});