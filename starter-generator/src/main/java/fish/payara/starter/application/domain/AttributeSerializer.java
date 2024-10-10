/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fish.payara.starter.application.domain;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

public class AttributeSerializer implements JsonbSerializer<Attribute> {

    @Override
    public void serialize(Attribute attribute, JsonGenerator generator, SerializationContext ctx) {
        generator.writeStartObject();

        // Always write the name and type
        generator.write("name", attribute.getName());
        generator.write("type", attribute.getType());

        if (attribute.isPrimaryKey()) {
            generator.write("primaryKey", true);
        }
        if (attribute.isMulti()) {
            generator.write("multi", true);
        }

        generator.writeEnd();
    }
}