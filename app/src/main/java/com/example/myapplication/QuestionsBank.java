package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class QuestionsBank {

    private static List<QuestionsList> topic1Questions()
    {
        final List<QuestionsList> questionsLists = new ArrayList<>();

        // Create object of QuestionsList class and pass a questions along with options and answer
        final QuestionsList qst1 = new QuestionsList("What is menstruation?", "The release of an unfertilized egg from the ovaries", "The shedding of the uterine lining", "The process of fertilization in the fallopian tubes", "The development of breast tissue", "The shedding of the uterine lining", "");
        final QuestionsList qst2 = new QuestionsList(
                "What is the average length of a menstrual cycle?",
                "7 days",
                "14 days",
                "28 days",
                "35 days",
                "28 days",
                ""
        );

        final QuestionsList qst3 = new QuestionsList(
                "What hormone is primarily responsible for regulating the menstrual cycle?",
                "Estrogen",
                "Progesterone",
                "Testosterone",
                "Follicle-stimulating hormone",
                "Progesterone",
                ""
        );

        final QuestionsList qst4 = new QuestionsList(
                "What is a common symptom of premenstrual syndrome (PMS)?",
                "Abdominal cramps",
                "Breast tenderness",
                "Mood swings",
                "All of the above",
                "All of the above",
                ""
        );

        final QuestionsList qst5 = new QuestionsList(
                "What is the medical term for the absence of menstruation?",
                "Menorrhagia",
                "Dysmenorrhea",
                "Amenorrhea",
                "Oligomenorrhea",
                "Amenorrhea",
                ""
        );

        final QuestionsList qst6 = new QuestionsList(
                "What is the purpose of menstrual hygiene products?",
                "To prevent pregnancy",
                "To manage menstrual flow and keep the person comfortable",
                "To reduce menstrual pain",
                "To regulate the menstrual cycle",
                "To manage menstrual flow and keep the person comfortable",
                ""
        );

        // add all questions to List<QuestionsList>
        questionsLists.add(qst1);
        questionsLists.add(qst2);
        questionsLists.add(qst3);
        questionsLists.add(qst4);
        questionsLists.add(qst5);
        questionsLists.add(qst6);

        return questionsLists;

    }

    private static List<QuestionsList> topic2Questions()
    {
        final List<QuestionsList> questionsLists = new ArrayList<>();

        // Create object of QuestionsList class and pass a questions along with options and answer

        final QuestionsList qst1 = new QuestionsList(
                "What is ovulation?",
                "The release of an unfertilized egg from the ovaries",
                "The shedding of the uterine lining",
                "The process of fertilization in the fallopian tubes",
                "The development of breast tissue",
                "The release of an unfertilized egg from the ovaries",
                ""
        );

        final QuestionsList qst2 = new QuestionsList(
                "What is the average length of a menstrual cycle?",
                "7 days",
                "14 days",
                "28 days",
                "35 days",
                "28 days",
                ""
        );

        final QuestionsList qst3 = new QuestionsList(
                "What hormone is responsible for preparing the uterus for pregnancy?",
                "Estrogen",
                "Progesterone",
                "Testosterone",
                "Follicle-stimulating hormone",
                "Progesterone",
                ""
        );

        final QuestionsList qst4 = new QuestionsList(
                "What is the fertile window?",
                "The time of the month when a woman is most likely to conceive",
                "The time when a woman experiences PMS symptoms",
                "The time when a woman's menstrual flow is heaviest",
                "The time when a woman's hormones are at their lowest levels",
                "The time of the month when a woman is most likely to conceive",
                ""
        );

        final QuestionsList qst5 = new QuestionsList(
                "What is the role of the fallopian tubes in fertility?",
                "To produce eggs",
                "To release hormones",
                "To nourish the developing embryo",
                "To provide a pathway for sperm to reach the egg",
                "To provide a pathway for sperm to reach the egg",
                ""
        );

        final QuestionsList qst6 = new QuestionsList(
                "What is the medical term for the inability to conceive or carry a pregnancy to term?",
                "Menopause",
                "Infertility",
                "Endometriosis",
                "Polycystic ovary syndrome",
                "Infertility",
                ""
        );

        // add all questions to List<QuestionsList>
        questionsLists.add(qst1);
        questionsLists.add(qst2);
        questionsLists.add(qst3);
        questionsLists.add(qst4);
        questionsLists.add(qst5);
        questionsLists.add(qst6);

        return questionsLists;

    }

    private static List<QuestionsList> topic3Questions()
    {
        final List<QuestionsList> questionsLists = new ArrayList<>();

        // Create object of QuestionsList class and pass a questions along with options and answer

        final QuestionsList qst1 = new QuestionsList(
                "What is dysmenorrhea?",
                "Severe menstrual pain and cramps",
                "Irregular menstrual cycles",
                "Absence of menstruation",
                "Excessive menstrual bleeding",
                "Severe menstrual pain and cramps",
                ""
        );

        final QuestionsList qst2 = new QuestionsList(
                "What are primary dysmenorrhea and secondary dysmenorrhea?",
                "Different types of menstrual products",
                "Different stages of the menstrual cycle",
                "Different medical conditions causing period pain",
                "Different levels of pain tolerance",
                "Different types of menstrual products",
                ""
        );

        final QuestionsList qst3 = new QuestionsList(
                "What are some common symptoms of period pain?",
                "Lower back pain",
                "Nausea and vomiting",
                "Headaches",
                "All of the above",
                "All of the above",
                ""
        );

        final QuestionsList qst4 = new QuestionsList(
                "What are some recommended ways to manage period pain?",
                "Over-the-counter pain relievers",
                "Applying heat to the abdomen",
                "Regular exercise",
                "All of the above",
                "All of the above",
                ""
        );

        final QuestionsList qst5 = new QuestionsList(
                "What is endometriosis?",
                "A condition where the uterine lining grows outside the uterus",
                "A condition where the uterine lining sheds more frequently",
                "A condition where the uterus is tilted backward",
                "A condition where the uterus is enlarged",
                "A condition where the uterine lining grows outside the uterus",
                ""
        );

        final QuestionsList qst6 = new QuestionsList(
                "When should someone seek medical help for severe period pain?",
                "If the pain interferes with daily activities",
                "If the pain lasts longer than a week",
                "If the pain is accompanied by excessive bleeding",
                "All of the above",
                "All of the above",
                ""
        );


        // add all questions to List<QuestionsList>
        questionsLists.add(qst1);
        questionsLists.add(qst2);
        questionsLists.add(qst3);
        questionsLists.add(qst4);
        questionsLists.add(qst5);
        questionsLists.add(qst6);

        return questionsLists;

    }

    private static List<QuestionsList> topic4Questions()
    {
        final List<QuestionsList> questionsLists = new ArrayList<>();

        // Create object of QuestionsList class and pass a questions along with options and answer

        final QuestionsList qst1 = new QuestionsList(
                "What are nonsteroidal anti-inflammatory drugs (NSAIDs)?",
                "Medications that reduce fever",
                "Medications that relieve pain and reduce inflammation",
                "Medications that prevent blood clots",
                "Medications that regulate hormonal balance",
                "Medications that relieve pain and reduce inflammation",
                ""
        );

        final QuestionsList qst2 = new QuestionsList(
                "Which of the following is a common over-the-counter NSAID used for period pain relief?",
                "Paracetamol (acetaminophen)",
                "Aspirin",
                "Ibuprofen",
                "Codeine",
                "Ibuprofen",
                ""
        );

        final QuestionsList qst3 = new QuestionsList(
                "How do NSAIDs work to alleviate period pain?",
                "By constricting blood vessels in the uterus",
                "By reducing prostaglandin production and inflammation",
                "By increasing the release of endorphins in the brain",
                "By relaxing uterine muscles",
                "By reducing prostaglandin production and inflammation",
                ""
        );

        final QuestionsList qst4 = new QuestionsList(
                "What are some potential side effects of NSAIDs?",
                "Upset stomach and indigestion",
                "Increased risk of bleeding",
                "Kidney problems",
                "All of the above",
                "All of the above",
                ""
        );

        final QuestionsList qst5 = new QuestionsList(
                "Are NSAIDs suitable for everyone?",
                "Yes, they can be used by anyone without any risks",
                "No, they should not be used by anyone",
                "Yes, but only by individuals with certain medical conditions",
                "No, they should be avoided during menstruation",
                "Yes, but only by individuals with certain medical conditions",
                ""
        );

        final QuestionsList qst6 = new QuestionsList(
                "What is the recommended dosage and frequency of NSAID use for period pain?",
                "Take as much as needed for complete pain relief",
                "Take the highest possible dosage at the start of menstruation",
                "Follow the recommended dosage instructions on the packaging",
                "Take only when the pain becomes unbearable",
                "Follow the recommended dosage instructions on the packaging",
                ""
        );


        // add all questions to List<QuestionsList>
        questionsLists.add(qst1);
        questionsLists.add(qst2);
        questionsLists.add(qst3);
        questionsLists.add(qst4);
        questionsLists.add(qst5);
        questionsLists.add(qst6);

        return questionsLists;

    }


    public static List<QuestionsList> getQuestions(String selectedTopicName) {
        switch (selectedTopicName) {
            case "topic1":
                return topic1Questions();
            case "topic2":
                return topic2Questions();
            case "topic3":
                return topic3Questions();
            default :
                return topic4Questions();
        }
    }


}
