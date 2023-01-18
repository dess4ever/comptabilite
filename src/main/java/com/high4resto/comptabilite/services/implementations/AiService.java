package com.high4resto.comptabilite.services.implementations;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.high4resto.comptabilite.documents.Society;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;

import jakarta.annotation.PostConstruct;

@Service
public class AiService {
    private OpenAiService oAIservice;    
    private String chatAccumulator;
    private  List<String> stopSequence;

    public String request(String prompt)
    {
        String reponse="";
        CompletionRequest completionRequest=CompletionRequest.builder()
        .model("text-davinci-003")
        .prompt(prompt)
        .temperature(0.9)
        .stop(this.stopSequence)
        .topP(1.0)
        .frequencyPenalty(0.0)
        .presencePenalty(0.6)
        .bestOf(1)
        .echo(true)
        .maxTokens(2048)
        .user("testing")
        .build();

        
        List<CompletionChoice>list=oAIservice.createCompletion(completionRequest).getChoices();
        for(CompletionChoice result:list)
        {
            reponse+=result;
        }
        int indexOfStart=reponse.indexOf(prompt)+prompt.length();
        int indexStop=reponse.indexOf(", index=0");
        String fReponse=reponse.substring(indexOfStart, indexStop);
        return fReponse;
    }
    
    public Society extract_Society(String prompt)
    {
        String reponse="";
        Society tpSociety=new Society();
        prompt+="\n\n";
        prompt+="| Nom de la société | Adresse en une ligne |  \n\n";
        CompletionRequest completionRequest = CompletionRequest.builder()
		.model("text-davinci-003")
		.prompt(prompt)
		.temperature(0.0)
		.frequencyPenalty(0.0)
		.maxTokens(2048)
		.topP(1.0)
		.echo(true)
		.user("testing")
		.build();
        List<CompletionChoice>list=oAIservice.createCompletion(completionRequest).getChoices();
		for(CompletionChoice result:list)
		{
			reponse+=result;
		}
		System.out.println(reponse);        
        return tpSociety;
    }
    
    public String chat(String prompt)
    {
        String reponse="";
        CompletionRequest completionRequest=CompletionRequest.builder()
        .model("text-davinci-003")
        .prompt(this.chatAccumulator+prompt)
        .temperature(0.9)
        .stop(this.stopSequence)
        .topP(1.0)
        .frequencyPenalty(0.0)
        .presencePenalty(0.6)
        .bestOf(1)
        .echo(true)
        .maxTokens(2048)
        .user("testing")
        .build();

        List<CompletionChoice>list=oAIservice.createCompletion(completionRequest).getChoices();
		for(CompletionChoice result:list)
		{
			reponse+=result;
        }
        int indexOfStart=reponse.indexOf(prompt)+prompt.length();
        int indexStop=reponse.indexOf(", index=0");
        String fReponse=reponse.substring(indexOfStart, indexStop);
        this.chatAccumulator+="\nIA:"+fReponse;
        return fReponse;
    }

    public void reset()
    {
        chatAccumulator="Ce qui suit est une conversation avec un assistant d'IA. L'assistant est serviable et pragmatique et évite de donné des liens web. L'assistant est un expert en comptabilités. Il n'aime pas répondre aux questions autres que sur la comptabilité. \nHuman: Hello, qui es-tu?\nAI:  Je suis une IA créée par OpenAI. Comment puis-je vous aider aujourd'hui ?\nHuman:";
    }

    @PostConstruct
    public void init()
    {
        String token = System.getenv("OPENAI_TOKEN");
        this.stopSequence=new ArrayList<>();
        this.stopSequence.add("Human");
        this.stopSequence.add("AI:");
        oAIservice = new OpenAiService(token,60);
        chatAccumulator="Ce qui suit est une conversation avec un assistant d'IA. L'assistant est serviable et pragmatique et évite de donné des liens web. L'assistant est un expert en comptabilités. Il n'aime pas répondre aux questions autres que sur la comptabilité. \nHuman: Hello, qui es-tu?\nAI:  Je suis une IA créée par OpenAI. Comment puis-je vous aider aujourd'hui ?\nHuman:";
    }
}
