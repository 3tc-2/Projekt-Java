

export default function Test() {
    return (
        <div className="test-container">
            <h1>Wygeneruj swój własny test</h1>
            <p>Tutaj znajdziesz opcję generowania własnych testów.</p>
            <div className="test-options">
                <input type="select" placeholder="Wybierz kategorię" />
                <input type="select" placeholder="Wybierz etap" />
                <input type="select" placeholder="Tryb nauki" />
                <input type="range" placeholder="Długość testu" />
            
            <button className="generate-test-button">Generuj test</button>

            </div>
        </div>
    );
            
}