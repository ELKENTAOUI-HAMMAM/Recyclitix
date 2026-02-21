package cours.iir4.smartrecyclebackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recycling-points")
public class RecyclingPointController {

        @GetMapping
        public List<Map<String, Object>> getRecyclingPoints(
                        @RequestParam double lat,
                        @RequestParam double lng,
                        @RequestParam int radius) {

                List<Map<String, Object>> points = new java.util.ArrayList<>();

                // --- MOROCCO (MAROC) ---
                addPoint(points, 1, "Marrakech Medina Sorting Center", "Avenue Mohammed VI, Marrakech", 31.6295,
                                -7.9811, "MIXED", "Plastic, Paper, Glass", "8am-18pm", "+212 524-111");
                addPoint(points, 2, "Eco-Center Majorelle", "Rue Yves Saint Laurent, Marrakech", 31.6424, -8.0028,
                                "GLASS", "Glass, Metal", "8am-20pm", "+212 524-222");
                addPoint(points, 3, "Casablanca Anfa Center", "Route de Mediouna, Casablanca", 33.5899, -7.6039,
                                "MIXED", "Plastic, Cardboard", "9am-18pm", "+212 522-333");
                addPoint(points, 4, "Agadir Green Point", "Quartier Industriel, Agadir", 30.4277, -9.5981, "PLASTIC",
                                "Plastic, Cans", "8am-17pm", "+212 528-444");
                addPoint(points, 5, "Tangier Med Recycling", "Tangier Med Port, Tangier", 35.7595, -5.8340, "MIXED",
                                "Metal, Glass", "24/7", "+212 539-555");

                // --- FRANCE ---
                addPoint(points, 6, "Paris Porte des Lilas", "Boulevard Mortier, Paris", 48.8789, 2.4063, "MIXED",
                                "All Recyclables", "7am-20pm", "+33 1 43 61");
                addPoint(points, 7, "Paris Quai d'Issy", "Quai d'Issy, Paris", 48.8351, 2.2741, "MIXED", "Wood, Metal",
                                "7am-19pm", "+33 1 45 57");
                addPoint(points, 8, "Lyon Vaise Espace Tri", "Rue de la Gare d'Eau, Lyon", 45.7828, 4.8055, "MIXED",
                                "Paper, Plastic", "8am-18pm", "+33 4 78 12");
                addPoint(points, 9, "Marseille Aygalades", "Chemin de la Commanderie, Marseille", 43.3456, 5.3822,
                                "MIXED", "Green Waste, Metal", "8am-19pm", "+33 4 91 00");

                // --- UK ---
                addPoint(points, 10, "London Camden Center", "Regis Road, London", 51.5457, -0.1477, "MIXED",
                                "Plastic, Glass", "8am-16pm", "+44 20 7974");
                addPoint(points, 11, "London Islington Reuse", "Hornsey Street, London", 51.5516, -0.1114, "MIXED",
                                "Furniture, Tech", "9am-16pm", "+44 20 7527");

                // --- SPAIN & ITALY ---
                addPoint(points, 12, "Madrid Estrella Denebola", "Calle Estrella Denebola, Madrid", 40.3951, -3.6823,
                                "MIXED", "All Recyclables", "8am-20pm", "+34 91 444");
                addPoint(points, 13, "Barcelona Punt Verd", "C Rovira i Virgili, Barcelona", 41.4333, 2.1833, "MIXED",
                                "Metals, Glass", "9am-19pm", "+34 93 333");
                addPoint(points, 14, "Rome AMA Mostacciano", "Via Riccardo Boschiero, Rome", 41.8080, 12.4280, "MIXED",
                                "Bulky Waste", "7am-13pm", "+39 06 06");
                addPoint(points, 15, "Milan Ricicleria Olgettina", "Via Olgettina 35, Milan", 45.5050, 9.2650, "MIXED",
                                "Construction, Metal", "8am-19pm", "+39 02 02");

                // --- GERMANY & TURKEY ---
                addPoint(points, 16, "Berlin Gradestraße", "Gradestraße 73, Berlin", 52.4589, 13.4326, "MIXED",
                                "All Recyclables", "7am-19pm", "+49 30 75");
                addPoint(points, 17, "Istanbul Kucukcekmece", "Atakent Mah, Istanbul", 41.0340, 28.7750, "MIXED",
                                "Plastic, Cans", "9am-17pm", "+90 212");

                // --- USA ---
                addPoint(points, 18, "NYC Sims Recycling", "2nd Ave, Brooklyn", 40.6651, -74.0022, "MIXED",
                                "Metal, Glass", "8am-16pm", "+1 718 499");
                addPoint(points, 19, "LA Green Recycling", "14th St, Los Angeles", 34.0321, -118.2435, "MIXED",
                                "Plastic, Cardboard", "8am-17pm", "+1 213 111");
                addPoint(points, 20, "SF Recycle Central", "Amador St, San Francisco", 37.7450, -122.3840, "MIXED",
                                "All Recyclables", "7am-16pm", "+1 415 222");
                addPoint(points, 21, "Chicago Ravenswood", "N Ravenswood, Chicago", 41.9990, -87.6740, "MIXED",
                                "Paper, Plastic", "8am-18pm", "+1 312 333");

                // --- CANADA ---
                addPoint(points, 22, "Toronto Bermondsey Depot", "Bermondsey Rd, Toronto", 43.7150, -79.3120, "MIXED",
                                "Hazardous, Mixed", "7am-18pm", "+1 416 444");
                addPoint(points, 23, "Vancouver Zero Waste", "Riverside Dr, Vancouver", 49.3050, -123.0180, "MIXED",
                                "All Recyclables", "8am-17pm", "+1 604 555");

                // --- ASIA ---
                addPoint(points, 24, "Tokyo Shinjuku Center", "Kabuki-cho, Tokyo", 35.6938, 139.7034, "MIXED",
                                "Cans, Paper", "9am-17pm", "+81 3 5273");
                addPoint(points, 25, "Beijing Green Hub", "Qingyundian, Beijing", 39.7080, 116.5120, "HAZARDOUS",
                                "Industrial Waste", "8am-18pm", "+86 10 111");
                addPoint(points, 26, "Shanghai Waste Hub", "Pudong, Shanghai", 31.2222, 121.5397, "MIXED",
                                "Plastic, Glass", "9am-17pm", "+86 21 222");
                addPoint(points, 27, "Singapore Eco-Park", "Tuas Ave, Singapore", 1.2980, 103.6264, "MIXED",
                                "Industrial Res.", "8am-20pm", "+65 6666");

                // --- MIDDLE EAST ---
                addPoint(points, 28, "Dubai Mirdif Oasis", "Mirdif Park, Dubai", 25.2167, 55.4167, "SMART",
                                "Plastics, Tech", "24/7", "+971 800");

                // --- SOUTH AMERICA ---
                addPoint(points, 29, "Sao Paulo Pontos Verde", "Avenida Paulista, S.P.", -23.5505, -46.6333, "MIXED",
                                "Selective Coll.", "8am-18pm", "+55 11 111");
                addPoint(points, 30, "Rio de Janeiro CRR", "Fazenda Botafogo, Rio", -22.9068, -43.1729, "MIXED",
                                "Industrial", "8am-17pm", "+55 21 222");

                // --- AFRICA ---
                addPoint(points, 31, "Johannesburg Denver", "Dwerg Street, J.B.", -26.2041, 28.0473, "MIXED",
                                "Plastic, Paper", "8am-16pm", "+27 11 333");
                addPoint(points, 32, "Cape Town Think Twice", "Waterfront, Cape Town", -33.9249, 18.4241, "MIXED",
                                "Household", "9am-17pm", "+27 21 444");

                // --- OCEANIA ---
                addPoint(points, 33, "Sydney Artarmon CRC", "Waltham St, Sydney", -33.8148, 151.1877, "MIXED",
                                "Chemicals, Metal", "8am-16pm", "+61 2 9439");

                // --- ADDITIONAL GLOBAL POINTS ---
                addPoint(points, 34, "Melbourne Recycling", "Fishermans Bend, Melbourne", -37.8282, 144.9197, "MIXED",
                                "Mixed Waste", "8am-16pm", "+61 3 9999");
                addPoint(points, 35, "Seoul Green Point", "Gangnam, Seoul", 37.4979, 127.0276, "SMART",
                                "Plastics, Paper", "24/7", "+82 2 111");
                addPoint(points, 36, "Bangkok Waste Hub", "Sukhumvit, Bangkok", 13.7367, 100.5231, "MIXED",
                                "Glass, Cans", "9am-18pm", "+66 2 222");
                addPoint(points, 37, "Moscow Eco-Center", "Presnensky, Moscow", 55.7558, 37.6173, "MIXED", "Multi-sort",
                                "9am-20pm", "+7 495");
                addPoint(points, 38, "Vienna MA 48", "Simmering, Vienna", 48.2082, 16.3738, "MIXED", "Eco-Sorting",
                                "7am-18pm", "+43 1 58817");
                addPoint(points, 39, "Zurich Entsorgung", "Werdhölzli, Zurich", 47.3769, 8.5417, "MIXED",
                                "High-tech sort", "7am-17pm", "+41 44 412");
                addPoint(points, 40, "Amsterdam Sort", "Noord, Amsterdam", 52.3676, 4.9041, "MIXED", "Plastic, Paper",
                                "8am-16pm", "+31 20");
                addPoint(points, 41, "Dublin Recycling", "Ballymount, Dublin", 53.3498, -6.2603, "MIXED", "Hazardous",
                                "9am-17pm", "+353 1");
                addPoint(points, 42, "Lisbon Ponto Verde", "Alcântara, Lisbon", 38.7223, -9.1393, "MIXED",
                                "Glass, Plastic", "8am-19pm", "+351 21");
                addPoint(points, 43, "Prague Sberny Dvur", "Karlin, Prague", 50.0755, 14.4378, "MIXED", "Mixed",
                                "8am-18pm", "+420 2");
                addPoint(points, 44, "Warsaw Eco-Station", "Mokotow, Warsaw", 52.2297, 21.0122, "MIXED",
                                "All materials", "8am-19pm", "+48 22");
                addPoint(points, 45, "Stockholm Sorfri", "Bromma, Stockholm", 59.3293, 18.0686, "MIXED",
                                "Automated sort", "7am-20pm", "+46 8");
                addPoint(points, 46, "Oslo Miljostasjon", "Grefsen, Oslo", 59.9139, 10.7522, "MIXED", "Glass, Tech",
                                "8am-18pm", "+47 22");
                addPoint(points, 47, "Helsinki Kierratys", "Kylasaari, Helsinki", 60.1699, 24.9384, "MIXED",
                                "Textiles, Tech", "9am-20pm", "+358 9");
                addPoint(points, 48, "Athens Green Point", "Piraeus, Athens", 37.9838, 23.7275, "MIXED", "Glass, Cans",
                                "8am-15pm", "+30 21");
                addPoint(points, 49, "Cairo Eco-Center", "Maadi, Cairo", 30.0444, 31.2357, "MIXED",
                                "Plastics, Cardboard", "9am-18pm", "+20 2");
                addPoint(points, 50, "Nairobi Waste Lab", "Industrial Area, Nairobi", -1.2921, 36.8219, "MIXED",
                                "Plastic, Metal", "8am-17pm", "+254 20");

                return points;
        }

        private void addPoint(List<Map<String, Object>> list, int id, String name, String addr, double lat, double lng,
                        String type, String mat, String hrs, String tel) {
                list.add(Map.of(
                                "id", id,
                                "name", name,
                                "address", addr,
                                "latitude", lat,
                                "longitude", lng,
                                "type", type,
                                "acceptedMaterials", Arrays.asList(mat.split(", ")),
                                "hours", hrs,
                                "contact", tel));
        }
}