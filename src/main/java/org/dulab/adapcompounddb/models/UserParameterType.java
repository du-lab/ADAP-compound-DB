package org.dulab.adapcompounddb.models;

import org.json.JSONArray;

import java.util.List;
import java.util.stream.Collectors;

public enum UserParameterType implements UserParameterTypeInterface {

    INTEGER {
        @Override
        public Object fromString(String string) {
            return Integer.valueOf(string);
        }

        @Override
        public String toString(Object object) {
            return Integer.toString((int) object);
        }
    },

    FLOAT {
        @Override
        public Object fromString(String string) {
            return Float.valueOf(string);
        }

        @Override
        public String toString(Object object) {
            return Float.toString((float) object);
        }
    },

    BOOLEAN {
        @Override
        public Object fromString(String string) {
            return Boolean.valueOf(string);
        }

        @Override
        public String toString(Object object) {
            return Boolean.toString((boolean) object);
        }
    },

    STRING {
        @Override
        public Object fromString(String string) {
            return string;
        }

        @Override
        public String toString(Object object) {
            return object.toString();
        }
    },

    CHROMATOGRAPHY_TYPE {
        @Override
        public Object fromString(String string) {
            return ChromatographyType.valueOf(string);
        }

        @Override
        public String toString(Object object) {
            return object.toString();
        }
    },

    INTEGER_LIST {
        @Override
        public Object fromString(String string) {
            return new JSONArray(string).toList()
                    .stream()
                    .map(x -> (long) x)
                    .collect(Collectors.toList());
        }

        @Override
        @SuppressWarnings("unchecked")
        public String toString(Object object) {
            return new JSONArray((List<Long>) object).toString();
        }
    }
}

interface UserParameterTypeInterface {
    Object fromString(String string);
    String toString(Object object);
}
