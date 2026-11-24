import { useState } from 'react';

export function Question({ question }) {
    const [selectedAnswerIndex, setSelectedAnswerIndex] = useState(null);
    const [isChecked, setIsChecked] = useState(null);
    const selectedAnswer = question.answers[selectedAnswerIndex];
    const handleButton = () => {
        if (selectedAnswer.isCorrect) {
            setIsChecked(true);
        }else {
            setIsChecked(false);
        }
    };

    return (
        <div className="question">
            <p>{question.text}</p>
            <div className="answers">
                {question.answers.map((answer, answerIndex) => (
                    <label key={answerIndex} className=
                    {`answer 
                    ${selectedAnswerIndex === answerIndex ? 'selected' : ''} 
                    ${isChecked && selectedAnswerIndex === answerIndex ? "correct" : ""} 
                    ${isChecked===false && selectedAnswerIndex === answerIndex ? "incorrect" : ""}`
                    }>
                        <input
                            type="radio"
                            name={`question-${question.index}`}
                            value={answerIndex}
                            checked={selectedAnswerIndex === answerIndex}
                            onChange={() => setSelectedAnswerIndex(answerIndex)}
                            disabled={isChecked !== null}
                        />
                        {answer.text}
                        
                    </label>
                ))}
            </div>
            <button onClick={() => handleButton()}>Sprawdź odpowiedź</button>
        </div>
    );
}
