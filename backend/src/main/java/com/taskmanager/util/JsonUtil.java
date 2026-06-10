package com.taskmanager.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilitário central para serialização/resposta JSON.
 * Usa Gson com adaptador para LocalDateTime.
 */
public class JsonUtil {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (com.google.gson.JsonSerializer<LocalDateTime>)
                    (src, type, ctx) -> new com.google.gson.JsonPrimitive(
                            src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .create();

    private JsonUtil() {}

    /** Serializa qualquer objeto para JSON string. */
    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    /** Desserializa JSON string para objeto do tipo T. */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    /** Monta resposta de sucesso: {"sucesso": true, "dados": ...} */
    public static String sucesso(Object dados) {
        JsonObject obj = new JsonObject();
        obj.addProperty("sucesso", true);
        obj.add("dados", GSON.toJsonTree(dados));
        return obj.toString();
    }

    /** Monta resposta de sucesso com mensagem textual. */
    public static String sucesso(String mensagem) {
        JsonObject obj = new JsonObject();
        obj.addProperty("sucesso", true);
        obj.addProperty("mensagem", mensagem);
        return obj.toString();
    }

    /** Monta resposta de erro: {"sucesso": false, "erro": "..."} */
    public static String erro(String mensagem) {
        JsonObject obj = new JsonObject();
        obj.addProperty("sucesso", false);
        obj.addProperty("erro", mensagem);
        return obj.toString();
    }

    /**
     * Envia resposta JSON na HttpServletResponse.
     * @param resp     response do servlet
     * @param status   código HTTP (ex: 200, 201, 400, 401, 500)
     * @param body     string JSON já montada
     */
    public static void enviar(HttpServletResponse resp, int status, String body)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setStatus(status);
        PrintWriter out = resp.getWriter();
        out.print(body);
        out.flush();
    }
}
