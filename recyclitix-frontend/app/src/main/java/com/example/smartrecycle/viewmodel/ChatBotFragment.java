package com.example.smartrecycle.viewmodel;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.smartrecycle.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChatBotFragment extends Fragment {
    private EditText inputMessage;
    private ImageButton sendButton;
    private LinearLayout chatContainer;
    private ProgressBar progressBar;
    private ScrollView scrollView;
    private List<ChatMessage> chatHistory;
    private Random random;

    private static class ChatMessage {
        String message;
        boolean isUser;
        long timestamp;

        ChatMessage(String message, boolean isUser) {
            this.message = message;
            this.isUser = isUser;
            this.timestamp = System.currentTimeMillis();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);
        
        inputMessage = view.findViewById(R.id.inputMessage);
        sendButton = view.findViewById(R.id.sendButton);
        chatContainer = view.findViewById(R.id.chatContainer);
        progressBar = view.findViewById(R.id.progressBar);
        scrollView = view.findViewById(R.id.scrollView);
        
        
        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
        
        chatHistory = new ArrayList<>();
        random = new Random();

        sendButton.setOnClickListener(v -> sendMessage());
        inputMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });

        
        addBotMessage("Bonjour ! Je suis RecycleBot 🤖, votre assistant pour le recyclage. Posez-moi vos questions sur le tri des déchets, les matériaux recyclables, ou tout autre sujet lié au recyclage !");
        
        
        addQuickSuggestions();
        
        return view;
    }

    private void addQuickSuggestions() {
        
        LinearLayout suggestionsLayout = new LinearLayout(requireContext());
        suggestionsLayout.setOrientation(LinearLayout.VERTICAL);
        suggestionsLayout.setPadding(16, 16, 16, 16);
        
        TextView suggestionsTitle = new TextView(requireContext());
        suggestionsTitle.setText("💡 Questions populaires :");
        suggestionsTitle.setTextSize(18);
        suggestionsTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        suggestionsTitle.setTextColor(getResources().getColor(R.color.textColor));
        suggestionsTitle.setPadding(0, 0, 0, 12);
        suggestionsLayout.addView(suggestionsTitle);
        
        String[] suggestions = {
            "Comment recycler le plastique ?",
            "Où jeter le verre ?",
            "Que faire des piles ?",
            "Comment trier les déchets ?",
            "Où trouver un centre de recyclage ?"
        };
        
        for (String suggestion : suggestions) {
            androidx.cardview.widget.CardView suggestionCard = new androidx.cardview.widget.CardView(requireContext());
            suggestionCard.setRadius(12);
            suggestionCard.setElevation(2);
            suggestionCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
            suggestionCard.setUseCompatPadding(true);
            
            TextView suggestionText = new TextView(requireContext());
            suggestionText.setText(suggestion);
            suggestionText.setTextSize(14);
            suggestionText.setTextColor(getResources().getColor(R.color.textColor));
            suggestionText.setPadding(16, 12, 16, 12);
            suggestionText.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            
            suggestionCard.addView(suggestionText);
            
            
            suggestionCard.setOnClickListener(v -> {
                inputMessage.setText(suggestion);
                sendMessage();
            });
            
            suggestionCard.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case android.view.MotionEvent.ACTION_DOWN:
                        suggestionCard.setCardBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                        break;
                    case android.view.MotionEvent.ACTION_UP:
                    case android.view.MotionEvent.ACTION_CANCEL:
                        suggestionCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                        break;
                }
                return false;
            });
            
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 4, 0, 4);
            suggestionCard.setLayoutParams(cardParams);
            
            suggestionsLayout.addView(suggestionCard);
        }
        
        
        chatContainer.addView(suggestionsLayout);
    }

    private void sendMessage() {
        String message = inputMessage.getText().toString().trim();
        if (TextUtils.isEmpty(message)) return;
        
        
        addUserMessage(message);
        inputMessage.setText("");
        
        
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            String response = getIntelligentResponse(message);
            addBotMessage(response);
        }, 800 + random.nextInt(400)); 
    }

    private void addUserMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true);
        chatHistory.add(chatMessage);
        addMessageToUI(chatMessage);
    }

    private void addBotMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false);
        chatHistory.add(chatMessage);
        addMessageToUI(chatMessage);
    }

    private void addMessageToUI(ChatMessage chatMessage) {
        
        androidx.cardview.widget.CardView messageCard = new androidx.cardview.widget.CardView(requireContext());
        messageCard.setRadius(16);
        messageCard.setElevation(4);
        messageCard.setUseCompatPadding(true);
        
        
        TextView messageView = new TextView(requireContext());
        messageView.setText(chatMessage.message);
        messageView.setTextSize(16);
        messageView.setPadding(20, 16, 20, 16);
        messageView.setMaxWidth((int) (getResources().getDisplayMetrics().widthPixels * 0.85f));
        messageView.setLineSpacing(4, 1.2f);
        
        
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(16, 8, 16, 8);
        messageCard.setLayoutParams(cardParams);
        
        if (chatMessage.isUser) {
            
            messageCard.setCardBackgroundColor(getResources().getColor(R.color.primaryColor));
            messageView.setTextColor(getResources().getColor(android.R.color.white));
            cardParams.gravity = android.view.Gravity.END;
            
            
            LinearLayout userLayout = new LinearLayout(requireContext());
            userLayout.setOrientation(LinearLayout.HORIZONTAL);
            userLayout.setGravity(android.view.Gravity.END);
            userLayout.setPadding(8, 0, 8, 0);
            
            TextView userIcon = new TextView(requireContext());
            userIcon.setText("👤");
            userIcon.setTextSize(20);
            userIcon.setPadding(0, 0, 8, 0);
            
            userLayout.addView(userIcon);
            userLayout.addView(messageView);
            messageCard.addView(userLayout);
            
        } else {
            
            messageCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
            messageView.setTextColor(getResources().getColor(R.color.textColor));
            cardParams.gravity = android.view.Gravity.START;
            
            
            LinearLayout botLayout = new LinearLayout(requireContext());
            botLayout.setOrientation(LinearLayout.HORIZONTAL);
            botLayout.setGravity(android.view.Gravity.START);
            botLayout.setPadding(8, 0, 8, 0);
            
            TextView botIcon = new TextView(requireContext());
            botIcon.setText("🤖");
            botIcon.setTextSize(20);
            botIcon.setPadding(0, 0, 8, 0);
            
            botLayout.addView(botIcon);
            botLayout.addView(messageView);
            messageCard.addView(botLayout);
        }
        
        chatContainer.addView(messageCard);
        
        
        messageCard.setAlpha(0f);
        messageCard.animate().alpha(1f).setDuration(300).start();
        
        
        new Handler().postDelayed(() -> {
            if (scrollView != null) {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 100);
    }

    private String getIntelligentResponse(String message) {
        String lowerMessage = message.toLowerCase();
        
        
        if (containsAny(lowerMessage, "bonjour", "salut", "hello", "hi", "coucou", "hey")) {
            String[] greetings = {
                "Bonjour ! Comment puis-je vous aider avec le recyclage aujourd'hui ? 🌱",
                "Salut ! Prêt(e) à recycler ? Posez-moi vos questions ! ♻️",
                "Hello ! Je suis là pour vous guider dans le tri des déchets ! 🌍"
            };
            return greetings[random.nextInt(greetings.length)];
        }

        
        if (containsAny(lowerMessage, "plastique", "bouteille plastique", "emballage plastique", "sachet plastique")) {
            return "♻️ **PLASTIQUE** :\n" +
                   "• Rincez les emballages avant de les jeter\n" +
                   "• Vérifiez le symbole de recyclage (triangle avec flèches)\n" +
                   "• Les bouteilles, flacons et emballages rigides vont dans le bac de tri\n" +
                   "• Les sacs plastique fins vont dans la poubelle normale\n" +
                   "• Évitez les plastiques souples et les films alimentaires";
        }

        
        if (containsAny(lowerMessage, "verre", "bouteille verre", "pot verre", "bocal")) {
            return "🫙 **VERRE** :\n" +
                   "• Le verre se recycle à l'infini !\n" +
                   "• Déposez dans le conteneur à verre (bouteilles, bocaux, pots)\n" +
                   "• Retirez les bouchons et couvercles\n" +
                   "• Ne mettez PAS : vaisselle, miroirs, vitres, ampoules\n" +
                   "• Rincez légèrement avant de jeter";
        }

        
        if (containsAny(lowerMessage, "papier", "carton", "journal", "magazine", "boîte carton")) {
            return "📦 **PAPIER & CARTON** :\n" +
                   "• Papiers, journaux, magazines, cartons\n" +
                   "• Pliez les cartons pour gagner de place\n" +
                   "• Retirez le scotch et les agrafes\n" +
                   "• Évitez : papiers gras, cartons souillés, papier photo\n" +
                   "• Les cartons de pizza très gras vont à la poubelle normale";
        }

        
        if (containsAny(lowerMessage, "métal", "canette", "aluminium", "boîte conserve", "ferraille")) {
            return "🥫 **MÉTAUX** :\n" +
                   "• Canettes, boîtes de conserve, emballages métalliques\n" +
                   "• Rincez les boîtes de conserve\n" +
                   "• Les couvercles et capsules vont aussi au recyclage\n" +
                   "• L'aluminium et l'acier sont 100% recyclables\n" +
                   "• Évitez les objets métalliques pointus ou dangereux";
        }

        
        if (containsAny(lowerMessage, "compost", "déchets organiques", "biodéchets", "épluchures", "marc café")) {
            return "🍃 **DÉCHETS ORGANIQUES** :\n" +
                   "• Épluchures de fruits et légumes\n" +
                   "• Marc de café, sachets de thé\n" +
                   "• Coquilles d'œufs, restes de pain\n" +
                   "• Feuilles mortes, herbe tondue\n" +
                   "• Évitez : viande, poisson, produits laitiers";
        }

        
        if (containsAny(lowerMessage, "électronique", "électrique", "pile", "batterie", "téléphone", "ordinateur")) {
            return "🔋 **DÉCHETS ÉLECTRONIQUES** :\n" +
                   "• Piles et batteries : points de collecte en magasin\n" +
                   "• Petits appareils : déchetterie ou magasins\n" +
                   "• Gros appareils : déchetterie ou reprise en magasin\n" +
                   "• Ne jetez JAMAIS les piles à la poubelle !\n" +
                   "• Les DEEE contiennent des métaux précieux recyclables";
        }

        
        if (containsAny(lowerMessage, "vêtement", "textile", "tissu", "linge", "chaussure")) {
            return "👕 **TEXTILES** :\n" +
                   "• Vêtements, chaussures, linge de maison\n" +
                   "• Déposez dans les conteneurs textiles\n" +
                   "• Même les vêtements abîmés peuvent être recyclés\n" +
                   "• Nettoyez et pliez avant de donner\n" +
                   "• Évitez les textiles souillés ou humides";
        }

        
        if (containsAny(lowerMessage, "médicament", "pilule", "sirop", "pommade")) {
            return "💊 **MÉDICAMENTS** :\n" +
                   "• Rapportez en pharmacie (même périmés)\n" +
                   "• Ne jetez JAMAIS à la poubelle ou dans les toilettes\n" +
                   "• Les emballages en carton vont au recyclage\n" +
                   "• Les flacons en verre vont au verre\n" +
                   "• Protection de l'environnement et de la santé !";
        }

        
        if (containsAny(lowerMessage, "huile", "graisse", "friture")) {
            return "🛢️ **HUILES** :\n" +
                   "• Huiles de friture : déchetterie ou points de collecte\n" +
                   "• Ne versez JAMAIS dans l'évier ou les toilettes\n" +
                   "• Laissez refroidir et versez dans un récipient fermé\n" +
                   "• 1 litre d'huile pollue 1000 litres d'eau !\n" +
                   "• Les huiles sont transformées en biocarburant";
        }

        
        if (containsAny(lowerMessage, "recycler", "recyclage", "tri", "trier")) {
            return "♻️ **COMMENT RECYCLER** :\n" +
                   "1. **Vérifiez** le type de déchet\n" +
                   "2. **Nettoyez** si nécessaire\n" +
                   "3. **Triez** par matériau\n" +
                   "4. **Déposez** dans le bon conteneur\n" +
                   "5. **En cas de doute**, demandez-moi !\n\n" +
                   "Les principaux matériaux recyclables : plastique, verre, papier, métal, organique.";
        }

        
        if (containsAny(lowerMessage, "centre recyclage", "déchetterie", "point collecte", "où jeter")) {
            return "🗺️ **POINTS DE COLLECTE** :\n" +
                   "• Utilisez la carte de l'application pour trouver les centres\n" +
                   "• Déchetteries pour gros volumes\n" +
                   "• Points de collecte en magasin (piles, cartouches)\n" +
                   "• Conteneurs de proximité (verre, textiles)\n" +
                   "• Consultez votre mairie pour les horaires";
        }

        
        if (containsAny(lowerMessage, "impact", "environnement", "pollution", "écologie", "planète")) {
            return "🌍 **IMPACT ENVIRONNEMENTAL** :\n" +
                   "• Le recyclage économise les ressources naturelles\n" +
                   "• Réduit la pollution de l'air et de l'eau\n" +
                   "• Diminue les émissions de CO2\n" +
                   "• Crée des emplois locaux\n" +
                   "• Chaque geste compte pour préserver notre planète !";
        }

        
        if (containsAny(lowerMessage, "application", "app", "fonction", "utiliser")) {
            return "📱 **FONCTIONNALITÉS DE L'APP** :\n" +
                   "• **Scan** : Identifiez vos déchets avec la caméra\n" +
                   "• **Carte** : Trouvez les points de collecte\n" +
                   "• **Historique** : Suivez vos actions de recyclage\n" +
                   "• **ChatBot** : Posez vos questions (c'est moi !)\n" +
                   "• **Profil** : Gérez vos préférences";
        }

        
        if (containsAny(lowerMessage, "merci", "thanks", "thank you")) {
            String[] thanks = {
                "Avec plaisir ! N'hésitez pas si vous avez d'autres questions ! 😊",
                "De rien ! Continuez à recycler, c'est bon pour la planète ! 🌱",
                "Ravi d'avoir pu vous aider ! Ensemble, protégeons l'environnement ! ♻️"
            };
            return thanks[random.nextInt(thanks.length)];
        }

        
        if (containsAny(lowerMessage, "quoi", "comment", "pourquoi", "quand", "où")) {
            return "🤔 Je ne suis pas sûr de comprendre votre question. " +
                   "Pouvez-vous reformuler ? Je peux vous aider avec :\n" +
                   "• Le tri des déchets (plastique, verre, papier, métal...)\n" +
                   "• Les points de collecte\n" +
                   "• L'impact environnemental\n" +
                   "• L'utilisation de l'application";
        }

        
        String[] defaultResponses = {
            "Je suis spécialisé dans le recyclage ! Posez-moi des questions sur le tri des déchets, les matériaux recyclables, ou les points de collecte. ♻️",
            "Hmm, je ne suis pas sûr de comprendre. Je peux vous aider avec le recyclage : plastique, verre, papier, métal, déchets organiques, etc. 🌱",
            "Intéressant ! Mais je suis RecycleBot, votre assistant recyclage. Demandez-moi plutôt comment trier vos déchets ou où les jeter ! 📦"
        };
        return defaultResponses[random.nextInt(defaultResponses.length)];
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
} 