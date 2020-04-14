import com.mongodb.*;
import com.mongodb.client.model.*;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import java.util.Arrays;
import java.util.List;

public class Main  {

    public static void main(String[] args) {

        //Conexión con MongoDB
        String host = new String("192.168.56.102");
        String port = new String("27017");
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://"+host+":"+port));

        //Collemos a base de datos que queremos. Hai que creala fora
        String dbName = new String("test");
        DB database = mongoClient.getDB(dbName);

        //Coleccion alumno. Hai que creala fora
        DBCollection colAlumno= database.getCollection("alumno");

        //Creamos o documento
        List<String> modulos = Arrays.asList("Acceso a datos", "Programacion");
        DBObject alumno = new BasicDBObject()
                .append("nome", "Manuel Varela")
                .append("direccion", new BasicDBObject()
                        .append("rua", "San Clemente")
                        .append("numero", 45))
                .append("modulos", modulos);

        //Insertamolo documento
        colAlumno.insert(alumno);
        System.out.println("Inserción realizada con éxito");

        //Insertamos varios documentos ao mesmo tempo
        DBObject alumno2 = new BasicDBObject()
                .append("nome", "Manuel Varela 2")
                .append("direccion", new BasicDBObject()
                        .append("rua", "San Clemente 2")
                        .append("numero", 46))
                .append("modulos", Arrays.asList("Acceso a datos"));

        DBObject alumno3 = new BasicDBObject()
                .append("nome", "Manuel Varela 3")
                .append("direccion", new BasicDBObject()
                        .append("rua", "San Clemente 3")
                        .append("numero", 47))
                .append("modulos", Arrays.asList("Programacion"));

        colAlumno.insert(Arrays.asList(alumno2,alumno3));
        System.out.println("Insercións realizadas con éxito");

        //Imos facer unha consulta
        System.out.println("Documentos con Manuel Varela: ");
        DBObject query = new BasicDBObject("nome", "Manuel Varela");
        DBCursor cursor = colAlumno.find(query);
        while (cursor.hasNext()){
            DBObject documento = cursor.next();
            System.out.println(documento.toString());
        }
        cursor.close();

        //Un só documento por consulta
        System.out.println("O primeiro documento con Manuel Varela: ");
        DBObject documento  = colAlumno.findOne(query);
        System.out.println(documento.toString());

        //Un só documento por consulta
        System.out.println("Documentos con Manuel Varela e Manuel Varela 2: ");
        Bson filter = Filters.or(Filters.eq("nome","Manuel Varela"),Filters.eq("nome","Manuel Varela 2"));
        DBObject query2 = new BasicDBObject(filter.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        System.out.println(query2.toString());
        DBCursor cursor2  = colAlumno.find(query2);
        while (cursor2.hasNext()){
            DBObject documentoAux = cursor2.next();
            System.out.println(documentoAux.toString());
        }
        cursor2.close();

        //Consultas en subddocumentos.
        System.out.println("Documentos con números de direccion maior ou igual a 46: ");
        Bson filter2 = Filters.gte("direccion.numero",46);
        DBObject query3 = new BasicDBObject(filter2.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        System.out.println(query3.toString());
        DBCursor cursor3  = colAlumno.find(query3);
        while (cursor3.hasNext()){
            DBObject documentoAux = cursor3.next();
            System.out.println(documentoAux.toString());
        }
        cursor3.close();

        //Consultas en arrays.
        System.out.println("Documentos con números de direccion maior ou igual a 46: ");
        Bson filter3 = Filters.eq("modulos","Acceso a datos");
        DBObject query4 = new BasicDBObject(filter3.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        System.out.println(query4.toString());
        DBCursor cursor4  = colAlumno.find(query4);
        while (cursor4.hasNext()){
            DBObject documentoAux = cursor4.next();
            System.out.println(documentoAux.toString());
        }
        cursor4.close();

        //proxeccións
        System.out.println("Proxeccion: ");
        DBCollectionFindOptions options1 = new DBCollectionFindOptions();
        Bson projectionAux = Projections.include(Arrays.asList("nome","modulos"));
        DBObject projection1 = new BasicDBObject(projectionAux.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        options1.projection(projection1);
        DBCursor cursor5  = colAlumno.find(new BasicDBObject(),options1);
        while (cursor5.hasNext()){
            DBObject documentoAux = cursor5.next();
            System.out.println(documentoAux.toString());
        }
        cursor5.close();

        //proxeccións, limit e skip
        System.out.println("Proxeccion, limit e skip: ");
        DBCollectionFindOptions options2 = new DBCollectionFindOptions();
        Bson projectionAux2 = Projections.include(Arrays.asList("nome","modulos"));
        DBObject projection2 = new BasicDBObject(projectionAux2.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        options2.projection(projection2);
        options2.limit(2);
        options2.skip(1);
        DBCursor cursor6  = colAlumno.find(new BasicDBObject(),options2);
        while (cursor6.hasNext()){
            DBObject documentoAux = cursor6.next();
            System.out.println(documentoAux.toString());
        }
        cursor6.close();

        System.out.println("Proxeccion, ski, limit e sort: ");
        DBCollectionFindOptions options3 = new DBCollectionFindOptions();
        Bson projectionAux3 = Projections.include(Arrays.asList("nome","modulos"));
        DBObject projection3 = new BasicDBObject(projectionAux2.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        options3.projection(projection3);
        options3.limit(2);
        options3.skip(1);
        Bson sortAux = Sorts.descending("nome");
        DBObject sort = new BasicDBObject(sortAux.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        options3.sort(sort);
        DBCursor cursor7  = colAlumno.find(new BasicDBObject(),options3);
        while (cursor7.hasNext()){
            DBObject documentoAux = cursor7.next();
            System.out.println(documentoAux.toString());
        }
        cursor7.close();

        //Actuaizacion
        System.out.println("Actualización: ");
        Bson filterUp = Filters.eq("nome","Manuel Varela 2");
        DBObject queryUp = new BasicDBObject(filterUp.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        Bson updateAux = Updates.set("nome","Manuel Varela Lopez");
        DBObject update = new BasicDBObject(updateAux.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        colAlumno.update(queryUp,update);
        DBCursor cursor8 = colAlumno.find(new BasicDBObject());
        while (cursor8.hasNext()){
            DBObject documentoAux = cursor8.next();
            System.out.println(documentoAux.toString());
        }
        cursor8.close();


        //Borramos todos los documentos
        colAlumno.remove(new BasicDBObject());



    }
}
