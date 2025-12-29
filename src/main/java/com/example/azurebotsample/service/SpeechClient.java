package com.example.azurebotsample.service;

import com.example.azurebotsample.model.xml.MsttsExpressAs;
import com.example.azurebotsample.model.xml.Speak;
import com.example.azurebotsample.model.xml.Voice;
import com.example.azurebotsample.model.xml.Prosody; // this is used to control the playback speed (experimental)
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.audio.AudioOutputStream;
import com.microsoft.cognitiveservices.speech.audio.PullAudioOutputStream;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.concurrent.Future;

import static com.microsoft.cognitiveservices.speech.util.SafeHandleType.AudioConfig;

@Service
@Slf4j
@NoArgsConstructor
public class SpeechClient {

    @Value("${azure.speech.subscription-key}")
    private String speechSubscriptionKey;

    @Value("${azure.speech.region}")
    private String resourceRegion;

    @Value("${azure.speech.endpoint}")
    private String endpointUrl;

    @Value("${azure.speech.voice-model}")
    private String voiceModel;

    @Value("${azure.speech.language}")
    private String lang;

    @Value("${azure.speech.style}")
    private String style;

    @Value("${azure.speech.role}")
    private String role;

    @Value("${azure.speech.style-degree}")
    private String styleDegree;

    public byte[] generateResponse(String responsePayload) {
        // Creates an instance of a speech synthesizer using speech configuration with
        // specified
        // endpoint and subscription key and default speaker as audio output.
        try (SpeechConfig config = SpeechConfig.fromEndpoint(new java.net.URI(endpointUrl), speechSubscriptionKey)) {
            // Set the voice name, refer to https://aka.ms/speech/voices/neural for full
            // list.
            String file_name = "outputaudio.wav";
            com.microsoft.cognitiveservices.speech.audio.AudioConfig audioConfig = com.microsoft.cognitiveservices.speech.audio.AudioConfig
                    .fromStreamOutput(AudioOutputStream.createPullStream());
            // var file_config =
            // com.microsoft.cognitiveservices.speech.audio.AudioConfig.fromStreamOutput(new
            // PullAudioOutputStream(0));
            // com.microsoft.cognitiveservices.speech.audio.AudioConfig audioConfig;
            // var audio_config = AudioConfig.FromStreamOutput(file_config);
            config.setSpeechSynthesisVoiceName(voiceModel);
            config.setOutputFormat(OutputFormat.Simple);
            config.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Riff16Khz16BitMonoPcm);
            try (SpeechSynthesizer synth = new SpeechSynthesizer(config, audioConfig)) {
                assert (config != null);
                assert (synth != null);

                int exitCode = 1;

                log.info("Sending payload to speech service : {}", responsePayload);
                // Future<SpeechSynthesisResult> task = synth.SpeakTextAsync(responsePayload);
                // generate SSML payload for synthesiser from input
                String xmlInputPayload = generateXMLPayload(responsePayload);
                Future<SpeechSynthesisResult> task = synth.SpeakSsmlAsync(xmlInputPayload);

                SpeechSynthesisResult result = task.get();
                assert (result != null);

                if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                    System.out.println("Speech synthesized to speaker for text [" + responsePayload + "]");
                    exitCode = 0;
                } else if (result.getReason() == ResultReason.Canceled) {
                    SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails
                            .fromResult(result);
                    System.out.println("CANCELED: Reason=" + cancellation.getReason());

                    if (cancellation.getReason() == CancellationReason.Error) {
                        System.out.println("CANCELED: ErrorCode=" + cancellation.getErrorCode());
                        System.out.println("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
                        System.out.println("CANCELED: Did you update the subscription info?");
                    }
                }
                if (exitCode == 0) {
                    return result.getAudioData();
                } else {
                    return null;
                }
            }

        } catch (Exception e) {
            log.info("Unhandled exception : {}", e.getMessage());
            return null;
        }
    }

    private String generateXMLPayload(String inputPayload) {
        try {
            // Create a Prosody object (experimental)
            Prosody prosody = new Prosody("-10.00%", null, null, inputPayload);

            // create MsttsExpressAs object
            MsttsExpressAs msttsExpressAs = new MsttsExpressAs(style, styleDegree, prosody);
            // Create Voice object
            Voice voice = new Voice(voiceModel, msttsExpressAs);
            // Create Speak object
            Speak speak = new Speak("1.0", lang, voice);
            // Create JAXBContext
            JAXBContext context = JAXBContext.newInstance(Speak.class);
            // Create Marshaller
            Marshaller marshaller = context.createMarshaller();
            // Set Marshaller properties for pretty printing
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            // To include the XML declaration (<?xml version="1.0" encoding="UTF-8"
            // standalone="yes"?>)
            // This is usually default, but can be explicitly set:
            // marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.FALSE);
            // Marshal to StringWriter (or System.out, FileOutputStream, etc.)
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(speak, stringWriter);
            // Print the XML
            String xmlOutput = stringWriter.toString();
            System.out.println("XML output : " + xmlOutput);
            return xmlOutput;
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
