package com.example.azurebotsample.service;

import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.audio.PullAudioOutputStream;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

import static com.microsoft.cognitiveservices.speech.util.SafeHandleType.AudioConfig;

@Service
@Slf4j
@NoArgsConstructor
public class SpeechClient {
    private final String speechSubscriptionKey = "5Clm4YR3KFhmDb1WfVa5cnTXPyDffNmxJsNC0RNcJWpfgu26yZWGJQQJ99BDACqBBLyXJ3w3AAAYACOGEj7V";
    private final String resourceRegion = "southeastasia";
    private final String endpointUrl = "https://southeastasia.api.cognitive.microsoft.com/";
    private final String voiceModel = "en-SG-LunaNeural";

    public byte[] generateResponse(String responsePayload){
        // Creates an instance of a speech synthesizer using speech configuration with
        // specified
        // endpoint and subscription key and default speaker as audio output.
        try(SpeechConfig config = SpeechConfig.fromEndpoint(new java.net.URI(endpointUrl), speechSubscriptionKey)){
            // Set the voice name, refer to https://aka.ms/speech/voices/neural for full
            // list.
            /*String file_name = "outputaudio.wav";
            var file_config = com.microsoft.cognitiveservices.speech.audio.AudioConfig.fromDefaultMicrophoneInput(file_name);
            var audio_config = AudioConfig.FromStreamOutput(new PullAudioOutputStream());*/
            config.setSpeechSynthesisVoiceName(voiceModel);
            config.setOutputFormat(OutputFormat.Simple);
            config.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Riff16Khz16BitMonoPcm);
            try (SpeechSynthesizer synth = new SpeechSynthesizer(config)){
                assert (config != null);
                assert (synth != null);

                int exitCode = 1;

                log.info("Sending payload to speech service : {}",responsePayload);
                Future<SpeechSynthesisResult> task = synth.SpeakTextAsync(responsePayload);

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
                }else {
                    return null;
                }
            }

        } catch (Exception e) {
            log.info("Unhandled exception : {}", e.getMessage());
            return null;
        }
    }
}
