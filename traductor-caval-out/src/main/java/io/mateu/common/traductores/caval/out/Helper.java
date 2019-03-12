package io.mateu.common.traductores.caval.out;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.io.BaseEncoding;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Helper {

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static LocalDate toDate(int n) {
        return LocalDate.of((n - n % 10000) / 10000, ((n % 10000) - n % 100) / 100, n % 100);
    }

    public static String toJson(Object o) throws JsonProcessingException {
        return mapper.writeValueAsString(o);
    }

    public static <T> T fromJson(String json, Class<T> c) throws IOException {
        if (json == null || "".equals(json)) json = "{}";
        return mapper.readValue(json, c);
    }

    public static Map<String,Object> hashmap(Object... args) {
        Map<String,Object> m = new HashMap<>();
        int pos = 0;
        Object o0 = null;
        for (Object o : args) {
            if (pos > 0 && pos % 2 == 1) {
                m.put("" + o0, o);
            } else {
                o0 = o;
            }
            pos++;
        }
        return m;
    }


    public static void main(String... args) throws IOException {
        Map<String, Object> d;
        System.out.println(toJson(d = hashmap("lan", "en", "agencyId", "3", "login", "DEMO", "pass", "1234")));
        System.out.println("Base64.getEncoder().encode()=" + Base64.getEncoder().encode(toJson(d).getBytes()));
        System.out.println("base64=" + BaseEncoding.base64().encode(toJson(d).getBytes()));
        System.out.println("base64url=" + BaseEncoding.base64Url().encode(toJson(d).getBytes()));
        System.out.println("base32hex=" + BaseEncoding.base32Hex().encode(toJson(d).getBytes()));

        System.out.println(Helper.toJson(new Credenciales(new String(BaseEncoding.base64().decode(BaseEncoding.base64().encode(toJson(d).getBytes()))))));
    }

}
