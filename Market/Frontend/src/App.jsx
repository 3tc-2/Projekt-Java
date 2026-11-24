

import  HomePage  from './HomePage.jsx';
const question = {
  index: 1,
  text: 'Który język jest używany razem z Reactem?',
  answers: [
    { text: 'Python', isCorrect: false },
    { text: 'JavaScript', isCorrect: true },
    { text: 'SQL', isCorrect: false },
  ],
};

function App() {


  return (
    <div className="App">
      <HomePage />
    </div>
  );
}

export default App;
