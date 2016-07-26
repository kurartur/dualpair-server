package com.artur.dualpair.server.service.socionics.test;

import com.artur.dualpair.server.domain.model.socionics.Sociotype;
import com.artur.dualpair.server.domain.model.socionics.test.Choice;
import com.artur.dualpair.server.domain.model.socionics.test.ChoicePair;
import com.artur.dualpair.server.infrastructure.persistence.repository.ChoicePairRepository;
import com.artur.dualpair.server.infrastructure.persistence.repository.ChoiceRepository;
import com.artur.dualpair.server.infrastructure.persistence.repository.SociotypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RemoteTestEvaluator implements TestEvaluator {

    private ChoiceRepository choiceRepository;
    private ChoicePairRepository choicePairRepository;
    private SociotypeRepository sociotypeRepository;

    public Sociotype evaluate(Map<String, String> choices) throws SocionicsTestException {
        EvaluateRequest evaluateRequest = createRequest();
        String response = evaluateRequest.post(createDataString(choices));
        Sociotype.Code1 sociotypeCode = parseResponse(response);
        Sociotype sociotype = sociotypeRepository.findByCode1(sociotypeCode);
        if (sociotype == null) {
            throw new SocionicsTestException(SocionicsTestException.sociotypeNotFound);
        }
        return sociotype;
    }

    private Sociotype.Code1 parseResponse(String response) throws SocionicsTestException {
        Pattern pattern = Pattern.compile("Ваш социотип: <a href=http:\\/\\/socionika\\.info\\/tip\\/([a-z]{3})\\.html>");
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return Sociotype.Code1.valueOf(matcher.group(1).toUpperCase());
        } else {
            throw new SocionicsTestException(SocionicsTestException.codeNotFoundInResponse);
        }
    }

    private String createDataString(Map<String, String> choices) throws SocionicsTestException {
        StringBuilder dataString = new StringBuilder();
        dataString.append("w1=on&w2=on&w4=on&w3=on&");
        for (Map.Entry<String, String> entry : choices.entrySet()) {
            Choice choice = choiceRepository.findByCode(entry.getValue());
            ChoicePair choicePair = choicePairRepository.findOne(Integer.valueOf(entry.getKey()));
            dataString
                    .append(choicePair.getRemoteId())
                    .append("=")
                    .append(choice.getRemoteValue())
                    .append("&");
        }
        dataString.append("tip=cheb");
        return dataString.toString();
    }

    protected EvaluateRequest createRequest() {
        return new EvaluateRequest();
    }

    @Autowired
    public void setChoiceRepository(ChoiceRepository choiceRepository) {
        this.choiceRepository = choiceRepository;
    }

    @Autowired
    public void setChoicePairRepository(ChoicePairRepository choicePairRepository) {
        this.choicePairRepository = choicePairRepository;
    }

    @Autowired
    public void setSociotypeRepository(SociotypeRepository sociotypeRepository) {
        this.sociotypeRepository = sociotypeRepository;
    }

    public static class EvaluateRequest {

        private static final String USER_AGENT = "Mozilla/5.0";
        private static final String URL_STRING = "http://your-ideal.com/cgi-bin/test.pl";

        public String post(String data) throws SocionicsTestException {
            try {
                URL url = new URL(URL_STRING);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", USER_AGENT);
                con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                con.setRequestProperty("Accept-Charset", "UTF-8");

                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(data);
                wr.flush();
                wr.close();

                int responseCode = con.getResponseCode();

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "windows-1251"));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            } catch (Exception e) {
                throw new SocionicsTestException(SocionicsTestException.evaluateRequestError, e);
            }
        }

    }

}
