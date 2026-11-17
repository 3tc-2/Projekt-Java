import { useState } from 'react'
import { useNavigate, Routes, Route } from 'react-router-dom';
import heroImg from './assets/hero.png'
import reactLogo from './assets/react.svg'
import viteLogo from './assets/vite.svg'
import './App.css'

function App() {
  const [count, setCount] = useState(0)

  const [login, setLogin] = useState("");
  const [haslo, setHaslo] = useState("");


  const handleChange = (event) => {
    setLogin(event.target.value);
  };

  const handleChange2 = (event) => {
    setHaslo(event.target.value);
  };


  const navigate = useNavigate();

  
  
  return (
    <>
      <div className='naglowek'>
        <img src='logo.png' alt='logo' className='logo'></img>
        </div>

        <div className='main'>


          <div className='czarny'> 

            <div className='lewyCzarny'>
              <h1>Zaloguj się do Divesbank</h1>
              <p className='text1'>identyfikatorem i haslem</p>
              <input
                    type="text"
                    id="id"
                    className='input'
                    value={login}
                    onChange={handleChange}
                    placeholder="Identyfikator"
                  />

                  <input
                    type="password"
                    id="haslo"
                    className='input'
                    value={haslo}
                    onChange={handleChange2}
                    placeholder="Hasło"
                  />

                  <button className='dalej' onClick={() => navigate("/dashboard")}>
                  Dalej
                </button>

                  {/* <Routes>
                      <Route path="/dashboard" element={<h1>Panel użytkownika</h1>} />
                    </Routes>
               */}
              </div>  


            <div className='prawyCzarny'>
              <img src='raczka.png' alt='raczka' className='raczka'></img>
              <a className='link' href='https://www.canva.com/design/DAHZ72-jSAc/-Ko89xVs4c0qUl1s47wZAA/edit'>Pomoc z logowaniem</a>

            </div>
          </div>

        </div>
    </>
  )
}

export default App
