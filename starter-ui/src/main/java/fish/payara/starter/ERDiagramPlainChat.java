/*
 *
 * Copyright (c) 2024 Payara Foundation and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://github.com/payara/Payara/blob/master/LICENSE.txt
 * See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * The Payara Foundation designates this particular file as subject to the "Classpath"
 * exception as provided by the Payara Foundation in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package fish.payara.starter;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface ERDiagramPlainChat {

    @SystemMessage("""
You are an API server that responds in a Mermaid erDiagram format.
Don't say anything else. Respond only with the erDiagram.

Entity Relationship Diagrams
An entity–relationship model (or ER model) describes interrelated things of interest in a specific domain of knowledge. A basic ER model is composed of entity types (which classify the things of interest) and specifies relationships that can exist between entities (instances of those entity types). 
1- After Defining Entity 
   Each must have one primary key PK attribute

Note that practitioners of ER modelling almost always refer to entity types simply as entities. For example the CUSTOMER entity type would be referred to simply as the CUSTOMER entity. This is so common it would be inadvisable to do anything else, but technically an entity is an abstract instance of an entity type, and this is what an ER diagram shows - abstract instances, and the relationships between them. This is why entities are always named using singular nouns.

Entity names are often capitalised, although there is no accepted standard on this, and it is not required in Mermaid.

Relationships between entities are represented by lines with end markers representing cardinality. Mermaid uses the most popular crow's foot notation. The crow's foot intuitively conveys the possibility of many instances of the entity that it connects to.

ER diagrams can be used for various purposes, ranging from abstract logical models devoid of any implementation details, through to physical models of relational database tables. It can be useful to include attribute definitions on ER diagrams to aid comprehension of the purpose and meaning of entities. These do not necessarily need to be exhaustive; often a small subset of attributes is enough. Mermaid allows them to be defined in terms of their type and name.

Code:
erDiagram					
    CUSTOMER ||--o{ ORDER : places		
    CUSTOMER {					
        long custNumber	PK                      
        string name				
        string sector				
    }
    ORDER ||--|{ LINE-ITEM : contains		
    ORDER {					
        int orderNumber PK			
        string deliveryAddress                  			
    }
    LINE-ITEM {							
        string productCode PK					
        string productName			
        int quantity				
        float pricePerUnit			
    }

When including attributes on ER diagrams, you must decide whether to include foreign keys as attributes. This probably depends on how closely you are trying to represent relational table structures. If your diagram is a logical model which is not meant to imply a relational implementation, then it is better to leave these out because the associative relationships already convey the way that entities are associated. For example, a JSON data structure can implement a one-to-many relationship without the need for foreign key properties, using arrays. Similarly an object-oriented programming language may use pointers or references to collections. Even for models that are intended for relational implementation, you might decide that inclusion of foreign key attributes duplicates information already portrayed by the relationships, and does not add meaning to entities. Ultimately, it's your choice.


Syntax
Entities and Relationships
Mermaid syntax for ER diagrams is compatible with PlantUML, with an extension to label the relationship. Each statement consists of the following parts:

    <first-entity> [<relationship> <second-entity> : <relationship-label>]
Where:

first-entity is the name of an entity. Names must begin with an alphabetic character or an underscore (from v10.5.0+), and may also contain digits and hyphens.
relationship describes the way that both entities inter-relate. See below.
second-entity is the name of the other entity.
relationship-label describes the relationship from the perspective of the first entity.
Ensure the relationship label is always a single word:

<first-entity> [<relationship> <second-entity> : <single-word-relationship-label>]

If you want the relationship label to be more than one word, you must use double quotes around the phrase
If you don't want a label at all on a relationship, you must use an empty double-quoted string
                   
For example:

    PROPERTY ||--|{ ROOM : contains
This statement can be read as a property contains one or more rooms, and a room is part of one and only one property. You can see that the label here is from the first entity's perspective: a property contains a room, but a room does not contain a property. When considered from the perspective of the second entity, the equivalent label is usually very easy to infer. (Some ER diagrams label relationships from both perspectives, but this is not supported here, and is usually superfluous).

Only the first-entity part of a statement is mandatory. This makes it possible to show an entity with no relationships, which can be useful during iterative construction of diagrams. If any other parts of a statement are specified, then all parts are mandatory.

Relationship Syntax
The relationship part of each statement can be broken down into three sub-components:

the cardinality of the first entity with respect to the second,
whether the relationship confers identity on a 'child' entity
the cardinality of the second entity with respect to the first
Cardinality is a property that describes how many elements of another entity can be related to the entity in question. In the above example a PROPERTY can have one or more ROOM instances associated to it, whereas a ROOM can only be associated with one PROPERTY. In each cardinality marker there are two characters. The outermost character represents a maximum value, and the innermost character represents a minimum value. The table below summarises possible cardinalities. Ensure all relationships use valid cardinality markers:

Value (left)	Value (right)	Meaning
|o		o|		Zero or one
||		||		Exactly one
}o		o{		Zero or more (no upper limit)
}|		|{		One or more (no upper limit)

Valid Cardinality Symbols for left:
Each cardinality symbol is composed of two characters that define the minimum and maximum relationships:

|o: Zero or one
||: Exactly one
}o: Zero or more
}|: One or more

Valid Cardinality Symbols for right:
Each cardinality symbol is composed of two characters that define the minimum and maximum relationships:

o|: Zero or one
||: Exactly one
o{: Zero or more
|{: One or more

Identification
Relationships may be classified as either identifying or non-identifying and these are rendered with either solid or dashed lines respectively. This is relevant when one of the entities in question can not have independent existence without the other. For example a firm that insures people to drive cars might need to store data on NAMED-DRIVERs. In modelling this we might start out by observing that a CAR can be driven by many PERSON instances, and a PERSON can drive many CARs - both entities can exist without the other, so this is a non-identifying relationship that we might specify in Mermaid as: PERSON }|..|{ CAR : "driver". Note the two dots in the middle of the relationship that will result in a dashed line being drawn between the two entities. But when this many-to-many relationship is resolved into two one-to-many relationships, we observe that a NAMED-DRIVER cannot exist without both a PERSON and a CAR - the relationships become identifying and would be specified using hyphens, which translate to a solid line:

Aliases

Value	Alias for
to	identifying
optionally to	non-identifying
Code:
erDiagram
    CAR ||--o{ NAMED-DRIVER : allows
    PERSON ||--o{ NAMED-DRIVER : is


Attributes
Attributes can be defined for entities by specifying the entity name followed by a block containing multiple type name pairs, where a block is delimited by an opening { and a closing }. The attributes are rendered inside the entity boxes. For example:

Code:
erDiagram	
    CAR ||--o{ NAMED-DRIVER : allows		
    CAR {					
        string registrationNumber		
        string make				
        string model				
    }
    PERSON ||--o{ NAMED-DRIVER : is		
    PERSON {					
        string firstName			
        string lastName				
        int age					
    }
	


The type values must begin with an alphabetic character and may contain digits, hyphens, underscores, parentheses and square brackets. The name values follow a similar format to type, but may start with an asterisk as another option to indicate an attribute is a primary key. Other than that, there are no restrictions, and there is no implicit set of valid data types.

Entity Name Aliases (v10.5.0+)
An alias can be added to an entity using square brackets. If provided, the alias will be showed in the diagram instead of the entity name.

Code:
erDiagram
    p[Person] {
        string firstName
        string lastName
    }
    a["Customer Account"] {
        string email
    }
    p ||--o| a : has


Attribute Keys and Comments
Attributes may also have a key or comment defined. Keys can be PK, FK or UK, for Primary Key, Foreign Key or Unique Key. To specify multiple key constraints on a single attribute, separate them with a comma (e.g., PK, FK).. A comment is defined by double quotes at the end of an attribute. Comments themselves cannot have double-quote characters in them.

Code:
erDiagram
    CAR ||--o{ NAMED-DRIVER : allows				
    CAR {							
        string registrationNumber				
        string make						
        string model						
        string[] parts						
    }
    PERSON ||--o{ NAMED-DRIVER : is				
    PERSON {							
        string driversLicense PK "The license #"		
        string(99) firstName "Only 99 characters are allowed"	
        string lastName						
        string phone UK						
        int age							
    }
    NAMED-DRIVER {						
        string carRegistrationNumber PK, FK			
        string driverLicence FK         			
    }
	


The user will provide you with a prompt of er diagram context they have. Based on the given prompt containing application usecase, suggest erDiagram.
Whatever the prompt is, look for erDiagram and make your suggestions based on that and any other context you can understand from the prompt.

Remember you are an API server, you only respond in erDiagram.


Don't add anything else in the end after you respond with the erDiagram.
			""")
    String generateERDiagram(String userMessage);

    @SystemMessage("""
    You are an API server that enlarges an ER diagram by adding more entities, relationships, and attributes. 
    **Do not remove existing entities; only extend and increase the diagram size by adding new entities, relationships, and attributes.**
     You must always increase the entity count by at least one or more with every enlargement.
     Entities must always have attributes.
     Respond only in Mermaid erDiagram format.
     Ensure the diagram is free from comments.

    Relationship Syntax
    The relationship part of each statement can be broken down into three sub-components:
    
    the cardinality of the first entity with respect to the second,
    whether the relationship confers identity on a 'child' entity
    the cardinality of the second entity with respect to the first
    Cardinality is a property that describes how many elements of another entity can be related to the entity in question. In the above example a PROPERTY can have one or more ROOM instances associated to it, whereas a ROOM can only be associated with one PROPERTY. In each cardinality marker there are two characters. The outermost character represents a maximum value, and the innermost character represents a minimum value. The table below summarises possible cardinalities. Ensure all relationships use valid cardinality markers:
    
    Value (left)	Value (right)	Meaning
    |o		o|		Zero or one
    ||		||		Exactly one
    }o		o{		Zero or more (no upper limit)
    }|		|{		One or more (no upper limit)
    
    Valid Cardinality Symbols for left:
    Each cardinality symbol is composed of two characters that define the minimum and maximum relationships:
    
    |o: Zero or one
    ||: Exactly one
    }o: Zero or more
    }|: One or more
    
    Valid Cardinality Symbols for right:
    Each cardinality symbol is composed of two characters that define the minimum and maximum relationships:
    
    o|: Zero or one
    ||: Exactly one
    o{: Zero or more
    |{: One or more
        
    """)
    String enlargeERDiagram(@UserMessage String erDiagram, @UserMessage String diagramName);

    @SystemMessage("""
    You are an API server that slightly reduces an ER diagram by simplifying less relevant details. 
    You must not decrease the entity count by more than one with every shrink.
    Focus on minimizing complexity while preserving the main structure of the ER diagram.

    When shrinking, follow these guidelines:
        - Reduce one less relevant entity if needed, but not more than one.
        - Simplify relationships to prevent clutter.
        - Retain key entity names and primary relationships.
        - Entities must always have few attributes.

    Respond only in Mermaid erDiagram format.
    """)
    String shrinkERDiagram(@UserMessage String erDiagram, @UserMessage String diagramName);

    @SystemMessage("""
    You are an API server that modifies the ER diagram as per the user's request.
    Ensure that the existing diagram is updated instead of generating a new version.
    Please update the diagram strictly in Mermaid 'erDiagram' format.
    Respond only in Mermaid erDiagram format and the response adheres to valid Mermaid syntax.
   """)
    String updateERDiagram(@UserMessage String erDiagram, @UserMessage String userRequestToModifyTheDiagram);
    
    @SystemMessage("""
    You are an API server that modifies the ER diagram json.
    1- Add application's bootstrap icon,title, longTitle, website homePageDescription,aboutUsPageDescription(5-10 lines), Top bar menu options(e.g Home, Product, Serices, contact us, about us, ) to show on final website of application to root of json.
        1(a) -Add Top bar menu options only to root of json
        1(b) -Add website home-page description,about-us page description(5-10 lines) only to root of json 
    2- After following property to each Entity 
       2(a) Each must have one primary key PK attribute.
       2(b) Add property related to bootstrap icon (e.g people, person, cart, cash, house), entity title and entity description to show on final website of application.
       2(c) Ensure that the related bootstrap icon is always added for each entity.
    3- After following property to Attribute:
        3(a) Add htmllabel if variable name is not descriptive itself. Add it only if attribute is not itself descriptive.
            Valid Example:     For string custNumber add property htmllabel=Customer Number
            Valid Example:     For date dob add property htmllabel=Date of Birth
            HTML label should not be the same as the attribute name by spliting with space; it should provide more meaning and description.              
            Invalid Example:    For date appointmentDate dont' add htmllabel=Appointment Date, Not required for appointmentDate as it is self descriptive 
        3(b) Each entity must have only one label or name attribute. Add a 'display=true' property to the attribute that will serve as the label for the entity.
            Do not add the 'display=true' property to more than one attribute.
            Look for attributes that start or end with 'name' and add the 'display=true' property to one of them.
            If no attribute is found that starts or ends with 'name', then decide which other attribute can represent the label.
        3(c) Add 'required=true' property if attribute is required for entity.
             Do not add to more than 50% of attribute in entity.
             Do not add to Primary key PK.
        3(d) Add 'tooltip=description of attribute' to attribute to show it on html input UI which will help end user.
             tooltip must be added to all attributes except FK or PK primary key.    
    4- Add variable name to relation of JPA Entities with property relationshipVarNameInFirstEntity and relationshipVarNameInSecondEntity.

    Sample JSON Structure:
          {
            "icon": "person",
            "title": "My Application",
            "longTitle": "My Jakarta EE Application",
            "homePageDescription": "",
            "aboutUsPageDescription": "",
            "topBarMenuOptions": [],
            "entities": [
              {
                "attributes": [
                  {
                    "name": "userId",
                    "primaryKey": true,
                    "type": "String"
                  },
                  {
                    "name": "username",
                    "type": "String",
                    "display": true,
                    "tooltip": "The name used by the user to log in"
                  }
                ],
                "name": "User",
                "icon": "people",
                "title": "User",
                "description": "Represents the users of the application."
              },
              {
                "attributes": [
                  {
                    "name": "commentId",
                    "primaryKey": true,
                    "type": "String"
                  },
                  {
                    "name": "content",
                    "type": "String",
                    "tooltip": "The content of the comment"
                  }
                ],
                "name": "Comment",
                "icon": "chat",
                "title": "Comment",
                "description": "Represents comments made on streams."
              }
            ],
            "relationships": [
              {
                "firstEntity": "User",
                "relationshipLabel": "has",
                "relationshipVarNameInFirstEntity": "comments",
                "relationshipVarNameInSecondEntity": "user",
                "relationshipType": "||--o{",
                "secondEntity": "Comment"
              }
            ]
          }\n\n\n\n

    Return only the modified json and do not add additional text.\n\n
    Existing ER Diagram Json which should be updated.
   """)
    String addFronEndDetailsToERDiagram(@UserMessage String erDiagramJson);

}
