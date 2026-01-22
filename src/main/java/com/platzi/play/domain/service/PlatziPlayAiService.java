package com.platzi.play.domain.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface PlatziPlayAiService {

    @UserMessage("""
            Genera un saludo de bienvenida a la plataforma de Gestión de Películas PlatziPlay {{plataform}}. 
            Usa menos de 120 caracteres y hazlo con el estilo de Platzi.
            """)
    String generateGreeting(@V("plataform")  String plataform);

    @SystemMessage(""" 
            Eres un experto en cine que recomienda peliculas personalizadas según los gustos del usuario.
            Debes recomendar máximo 3 peliculas.
            No incluyas peliculas que estén por fuera de la plataforma PlatziPlay.
    """)
    String generateMoviesSuggestion(@UserMessage String userMenssage);
}
