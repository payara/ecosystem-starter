/**
 * Copyright 2024 the original author or authors from the Jeddict project (https://jeddict.github.io/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
package fish.payara.starter.application.util;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Gaurav Gupta
 */
public class JPAUtil {

    private static final Set<String> JPA_RESERVED_KEYWORDS = Set.of(
            "ENTITY", "TRANSACTION", "PERSISTENCE", "QUERY", "TABLE", "COLUMN", "ID", "VERSION",
            "JOINCOLUMN", "MAPPEDBY", "ONETOMANY", "MANYTOONE", "ONETOONE", "MANYTOMANY", "CASCADETYPE",
            "FETCHTYPE", "ENTITYMANAGER", "NAMEDQUERY", "QUERYHINT", "EMBEDDABLE", "EMBEDDED",
            "TEMPORAL", "TEMPORALTYPE", "ENUMTYPE", "INHERITANCE", "DISCRIMINATORCOLUMN", "INHERITANCETYPE",
            "PREPERSIST", "POSTPERSIST", "PREUPDATE", "POSTUPDATE", "PREREMOVE", "POSTREMOVE",
            "PRELOAD", "POSTLOAD", "ACCESS", "ACCESSTYPE", "ATTRIBUTEOVERRIDE", "ASSOCIATIONOVERRIDE",
            "ELEMENTCOLLECTION", "ENUMERATED", "SEQUENCEGENERATOR", "TABLEGENERATOR", "GENERATEDVALUE",
            "GENERATIONTYPE", "IDCLASS", "JOINTABLE"
    );

    private static final Set<String> SQL_RESERVED_KEYWORDS = Set.of(
            "ADD", "ALL", "ALTER", "AND", "ANY", "AS", "ASC", "BACKUP", "BEGIN", "BETWEEN", "BIGINT", "BINARY",
            "BIT", "BY", "CASCADE", "CASE", "CHAR", "CHECK", "COLUMN", "CONSTRAINT", "CREATE", "CROSS",
            "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "DATABASE", "DEC", "DECIMAL",
            "DEFAULT", "DELETE", "DESC", "DISTINCT", "DOUBLE", "DROP", "ELSE", "END", "ESCAPE", "EXISTS",
            "FALSE", "FETCH", "FLOAT", "FOR", "FOREIGN", "FROM", "FULL", "GROUP", "HAVING", "IF", "IN", "INDEX",
            "INNER", "INSERT", "INT", "INTEGER", "INTERSECT", "INTO", "IS", "JOIN", "KEY", "LEFT", "LIKE", "LIMIT",
            "NOT", "NULL", "OFFSET", "ON", "OR", "ORDER", "OUTER", "PRIMARY", "PROCEDURE", "REAL", "REFERENCES",
            "RIGHT", "ROLLBACK", "SELECT", "SET", "SMALLINT", "TABLE", "THEN", "TIME", "TIMESTAMP", "TO",
            "TRANSACTION", "TRUE", "UNION", "UNIQUE", "UPDATE", "USING", "VALUES", "VARCHAR", "VIEW", "WHERE",
            "WITH", "WORK", "YEAR"
    );

    private static final Set<String> MYSQL_RESERVED_KEYWORDS = Set.of(
            "ACCESSIBLE", "ADD", "ALL", "ALTER", "ANALYZE", "AND", "AS", "ASC", "ASENSITIVE", "BEFORE",
            "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOOL", "BOOLEAN", "BY", "CALL", "CASCADE",
            "CASE", "CHANGE", "CHAR", "CHARACTER", "CHECK", "COLLATE", "COLUMN", "CONDITION",
            "CONNECTION", "CONSTRAINT", "CONTINUE", "CONVERT", "CREATE", "CROSS", "CURRENT_DATE",
            "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "DATABASE", "DATABASES",
            "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DEC", "DECIMAL",
            "DECLARE", "DEFAULT", "DELAYED", "DELETE", "DESC", "DESCRIBE", "DETERMINISTIC",
            "DISTINCT", "DISTINCTROW", "DOUBLE", "DROP", "DUAL", "EACH", "ELSE", "ELSEIF", "EMPTY",
            "ENCLOSED", "ESCAPED", "EXISTS", "EXIT", "EXPLAIN", "FALSE", "FETCH", "FLOAT",
            "FOR", "FORCE", "FOREIGN", "FROM", "FULLTEXT", "GRANT", "GROUP", "HAVING",
            "HIGH_PRIORITY", "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "IF", "IGNORE",
            "IN", "INOUT", "INNER", "INPLACE", "INSERT", "INT", "INT1", "INT2", "INT3", "INT4",
            "INT8", "INTEGER", "INTERVAL", "INTO", "IS", "ITERATE", "JOIN", "KEY", "KEYS",
            "KILL", "LEFT", "LIKE", "LIMIT", "LINEAR", "LINES", "LOAD", "LOCALTIME",
            "LOCALTIMESTAMP", "LOCK", "LONG", "LONGBLOB", "LONGTEXT", "LOOP", "LOW_PRIORITY",
            "MATCH", "MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT", "MIDDLEINT", "MINUTE_MICROSECOND",
            "MINUTE_SECOND", "MOD", "MODIFIES", "NATURAL", "NOT", "NO_WRITE_TO_BINLOG",
            "NULL", "ON", "OPTIMIZE", "OPTION", "OPTIONALLY", "OR", "ORDER", "OUT", "OUTER",
            "OUTFILE", "PARTITION", "PRIMARY", "PROCEDURE", "PURGE", "RANGE", "READ",
            "READS", "REAL", "REFERENCES", "REGEXP", "RELEASE", "RENAME", "REPLACE",
            "RESTRICT", "RETURN", "REVOKE", "RIGHT", "RLIKE", "ROW", "ROWS", "SELECT",
            "SET", "SHOW", "SMALLINT", "SPATIAL", "SPECIFIC", "SQL", "SQLEXCEPTION",
            "SQLSTATE", "SQLWARNING", "STRAIGHT_JOIN", "TABLE", "TABLES", "TABLESPACE",
            "THEN", "TIME", "TIMESTAMP", "TIMESTAMPDIFF", "TIMESTAMPADD", "TO",
            "TRIGGER", "TRUE", "UNION", "UNIQUE", "UNLOCK", "UNSIGNED", "UPDATE",
            "USAGE", "USE", "USING", "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP",
            "VALUES", "VARBINARY", "VARCHAR", "VARCHARACTER", "VARYING", "WHEN",
            "WHENEVER", "WHERE", "WHILE", "WITH", "WRITE", "X509", "XML", "YEAR_MONTH"
    );

    private static final Set<String> POSTGRESQL_RESERVED_KEYWORDS = Set.of(
            "ABORT", "ABS", "ALIAS", "ALL", "ALTER", "AND", "ANY", "ARRAY", "AS", "ASC",
            "ASSERTION", "AT", "AUTHORIZATION", "BACKWARD", "BEFORE", "BEGIN", "BETWEEN",
            "BOTH", "CASE", "CAST", "CHECK", "COLLATE", "COLUMN", "CONCURRENTLY", "CONSTRAINT",
            "CREATE", "CROSS", "CURRENT", "CURRENT_DATE", "CURRENT_ROLE", "CURRENT_TIME",
            "CURRENT_USER", "DEFAULT", "DEFERRABLE", "DEFERRED", "DELETE", "DESC", "DISTINCT",
            "DO", "ELSE", "END", "EXCEPT", "EXCLUDE", "EXISTS", "EXTRACT", "FALSE",
            "FETCH", "FOR", "FOREIGN", "FROM", "FULL", "GRANT", "GROUP", "HAVING",
            "ILIKE", "IN", "INHERIT", "INOUT", "INSERT", "INT", "INTERSECT", "INTO",
            "IS", "ISNULL", "JOIN", "KEY", "LEFT", "LIKE", "LIMIT", "LISTEN", "LOAD",
            "LOCAL", "LOCK", "MINUTE", "MONTH", "NAMES", "NOT", "NOTNULL", "NULL",
            "OFFSET", "ON", "ONLY", "OPEN", "OR", "ORDER", "OUTER", "OVER", "PARTITION",
            "PLACING", "PRIMARY", "RETURN", "REVOKE", "RIGHT", "ROLLBACK", "ROW",
            "ROWS", "SELECT", "SESSION_USER", "SET", "TABLE", "TABLES", "THEN",
            "TO", "UNION", "UNIQUE", "UPDATE", "USER", "USING", "VERBOSE",
            "VIEW", "WHEN", "WHERE", "WITH"
    );

    private static final Set<String> SQLSERVER_RESERVED_KEYWORDS = Set.of(
            "ADD", "ALL", "ALTER", "AND", "ANY", "AS", "ASC", "AUTHORIZATION", "BACKUP",
            "BEGIN", "BETWEEN", "BINARY", "BIT", "BREAK", "BROWSE", "BULK", "BY",
            "CASCADE", "CASE", "CHECK", "CHECKPOINT", "CLOSE", "CLUSTERED", "COALESCE",
            "COLUMN", "COMMIT", "COMPUTE", "CONSTRAINT", "CONTAINS", "CONVERT",
            "CREATE", "CROSS", "CURRENT", "CURRENT_DATE", "CURRENT_TIMESTAMP",
            "CURSOR", "DATABASE", "DBCC", "DEALLOCATE", "DECLARE", "DEFAULT",
            "DELETE", "DENY", "DESC", "DISTINCT", "DOUBLE", "DROP", "DUMP", "ELSE",
            "END", "ERRLVL", "ESCAPE", "EXISTS", "EXIT", "EXTERNAL", "FETCH",
            "FILE", "FILLFACTOR", "FOR", "FOREIGN", "FROM", "FULL", "GOTO",
            "GRANT", "GROUP", "HAVING", "HOLDLOCK", "IDENTITY", "IDENTITYCOL",
            "IF", "IN", "INDEX", "INNER", "INSERT", "INTERSECT", "INTO",
            "IS", "JOIN", "KEY", "KILL", "LEFT", "LIKE", "LINENO", "MERGE",
            "NATIONAL", "NOCHECK", "NONCLUSTERED", "NOT", "NULL", "OFF",
            "OFFSETS", "ON", "OPEN", "OPTION", "OR", "ORDER", "OUTER",
            "OVER", "PERCENT", "PIVOT", "PLAN", "PRIMARY", "PRINT",
            "PROC", "PROCEDURE", "RAISERROR", "READ", "READTEXT",
            "RECONFIGURE", "REFERENCES", "REPEATABLE", "RESTRICT",
            "RETURN", "REVERT", "REVOKE", "RIGHT", "ROLLBACK",
            "ROWCOUNT", "ROWS", "SAVE", "SCHEMA", "SELECT", "SESSION_USER",
            "SET", "SHUTDOWN", "SOME", "TABLE", "TABLESAMPLE", "THEN",
            "TO", "TOP", "TRAN", "TRANSACTION", "TRIGGER", "TRUNCATE",
            "UNION", "UNIQUE", "UPDATE", "USE", "USER", "VALUES",
            "VIEW", "WAITFOR", "WHEN", "WHERE", "WHILE", "WITH",
            "WITHIN", "WORK", "WRITE", "XOR"
    );

    private static final Set<String> ORACLE_RESERVED_KEYWORDS = Set.of(
            "ACCESS", "ADD", "ALL", "ALTER", "AND", "ANY", "AS", "ASC", "AUDIT",
            "BETWEEN", "BFILE", "BINARY_FLOAT", "BINARY_DOUBLE", "BLOB",
            "BOOLEAN", "BY", "CHAR", "CHARACTER", "CHECK", "CLUSTER",
            "COLUMN", "COMMIT", "CONNECT", "CREATE", "CURRENT",
            "DATE", "DAY", "DELETE", "DESC", "DISTINCT", "DOUBLE",
            "DROP", "ELSE", "END", "EXISTS", "EXTRACT", "FALSE",
            "FLOAT", "FOR", "FROM", "FULL", "GRANT", "GROUP",
            "HAVING", "IDENTIFIED", "IF", "IN", "INDEX", "INSERT",
            "INTO", "IS", "JOIN", "LEVEL", "LIKE", "LONG",
            "MAX", "MIN", "MONTH", "NATURAL", "NOT", "NULL",
            "NUMBER", "OF", "ON", "OPTION", "OR", "ORDER",
            "PLS_INTEGER", "PRIMARY", "PROCEDURE", "PUBLIC", "RAW",
            "RENAME", "REPLACE", "SELECT", "SESSION", "SET",
            "TABLE", "THEN", "TO", "TRIGGER", "TRUE", "UNION",
            "UPDATE", "USER", "VALIDATE", "VIEW", "WHEN", "WHERE",
            "WITH"
    );

    private static final Set<String> SQLITE_RESERVED_KEYWORDS = Set.of(
            "ABORT", "ACTION", "ADD", "AFTER", "ALL", "ALTER", "ANALYZE", "AND", "AS",
            "ASC", "ATTACH", "AUTOINCREMENT", "BEFORE", "BEGIN", "BETWEEN", "BY",
            "CASCADE", "CASE", "CHECK", "COLLATE", "COLUMN", "COMMIT", "CONFLICT",
            "CONSTRAINT", "CREATE", "CROSS", "CURRENT_DATE", "CURRENT_TIME",
            "CURRENT_TIMESTAMP", "DATABASE", "DEFAULT", "DEFERRABLE", "DELETE",
            "DESC", "DETACH", "DISTINCT", "DO", "DROP", "ELSE", "END",
            "EXCEPT", "EXCLUDE", "EXISTS", "EXPR", "FAIL", "FOR", "FOREIGN",
            "FROM", "FULL", "GLOB", "GROUP", "HAVING", "IF", "IGNORE",
            "IMMEDIATE", "IN", "INDEX", "INDEXED", "INITIALLY", "INNER",
            "INSERT", "INSTEAD", "INTERSECT", "INTO", "IS", "ISNULL",
            "JOIN", "KEY", "LEFT", "LIKE", "LIMIT", "MATCH", "NATURAL",
            "NOT", "NOTNULL", "NULL", "OF", "OFFSET", "ON", "OR",
            "ORDER", "PRAGMA", "RAISE", "RECURSIVE", "REFERENCES",
            "REGEXP", "REINDEX", "REPLACE", "RESTRICT", "ROLLBACK",
            "ROW", "ROWS", "SAVEPOINT", "SELECT", "SET", "TABLE",
            "TEMP", "TEMPORARY", "THEN", "TO", "TRANSACTION",
            "TRIGGER", "UNION", "UNIQUE", "UPDATE", "USING", "VACUUM",
            "VALUES", "VIEW", "WHEN", "WHERE", "WITH", "WITHOUT"
    );

    public static final Set<String> ALL_RESERVED_KEYWORDS = new HashSet<>();

    static {
        ALL_RESERVED_KEYWORDS.addAll(JPA_RESERVED_KEYWORDS);
        ALL_RESERVED_KEYWORDS.addAll(SQL_RESERVED_KEYWORDS);
        ALL_RESERVED_KEYWORDS.addAll(MYSQL_RESERVED_KEYWORDS);
        ALL_RESERVED_KEYWORDS.addAll(POSTGRESQL_RESERVED_KEYWORDS);
        ALL_RESERVED_KEYWORDS.addAll(SQLSERVER_RESERVED_KEYWORDS);
        ALL_RESERVED_KEYWORDS.addAll(ORACLE_RESERVED_KEYWORDS);
        ALL_RESERVED_KEYWORDS.addAll(SQLITE_RESERVED_KEYWORDS);
    }
}
