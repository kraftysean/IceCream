package com.tasty;

import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.Charge;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/Sales")
public class IcecreamServlet extends HttpServlet {

    private static final String STRIPE_SECRET_KEY = "sk_test_1QfsU0IJ9qZVOqhROVIZIJKd";  // Update to live key for production
    public static final String CURRENCY = "cad";

    private static final Logger logger = Logger.getLogger(IcecreamServlet.class.getName());

    public IcecreamServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.log(Level.INFO, request.getRequestURI());
        ServletContext context = getServletContext();
        RequestDispatcher rd = context.getRequestDispatcher("/homepage.html");
        rd.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.log(Level.INFO, request.getRequestURI());
        try {
            int length = request.getContentLength();
            byte[] input = new byte[length];
            ServletInputStream sin = request.getInputStream();
            int c, count = 0;
            while ((c = sin.read(input, count, input.length - count)) != -1) {
                count += c;
            }
            sin.close();

            String receivedString = new String(input);
            logger.log(Level.INFO, "Http-Output: \n" + receivedString);
            JSONObject reqObj;
            int amount = 0;
            String tokenId = "", name = "", flavour = "", style = "";
            try {
                reqObj = new JSONObject(receivedString);
                tokenId = reqObj.getString("stripe-token");
                amount = reqObj.getInt("amount");
                name = reqObj.getString("name");
                flavour = reqObj.getString("flavour");
                style = reqObj.getString("style");
            } catch (JSONException e) {
                logger.log(Level.SEVERE, e.toString());
            }

            // Initiate authorization callout to Stripe
            String stripeResult = stripeChargeRequest(tokenId, amount);
            logger.log(Level.INFO, stripeResult);
            // Write content to Text File (but consider using MongoDB in future)
             writeToFile(name, flavour, style, stripeResult);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");

            OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
            writer.write(stripeResult);
            writer.flush();
            writer.close();
        } catch (IOException | APIException | InvalidRequestException | JSONException | CardException | APIConnectionException | AuthenticationException e) {
            try {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print(e.getMessage());
                response.getWriter().close();
            } finally {
                logger.log(Level.SEVERE, e.toString());
            }
        }

    }

    private String stripeChargeRequest(String tokenId, int amount) throws CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException, JSONException {
        Stripe.apiKey = STRIPE_SECRET_KEY;

        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("source", tokenId);
        chargeParams.put("amount", amount);
        chargeParams.put("currency", CURRENCY);

        Charge charge = Charge.create(chargeParams);
        String result = charge.getStatus();
        return result;
    }

    private void writeToFile(String name, String flavour, String style, String result) throws IOException {
        String absolutePath = getServletContext().getRealPath("/");
        System.out.println("Path is:- " + absolutePath);
        // The name of the file to open.
        String fileName = absolutePath + "ice-cream-orders.txt";

        if (result.equalsIgnoreCase("succeeded")) {
            try {
                // Assume default encoding.
                FileWriter fileWriter = new FileWriter(fileName, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(name);
                bufferedWriter.newLine();
                bufferedWriter.write(flavour);
                bufferedWriter.newLine();
                bufferedWriter.write(style);
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                // Always close files.
                bufferedWriter.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, ex.toString());
            }
        }
    }
}