import './App.css';
import {BrowserRouter,Routes,Route} from 'react-router-dom';
import { useEffect, useState } from 'react';
import axios from 'axios';
import PostMain from './components/PostMain';
import PostParis from './components/PostParis';
import PostLondon from './components/PostLondon';
import PostAbout from './components/PostAbout';
// $.ajax와 거의 비슷, 서버에 데이터 요청시 비동기적으로 요청하려고 씀

function App() {

  const [placeList, setPlaceList] = useState([]);

    useEffect(() => {
      // 마운트 시 한 번 호출
      selectAll();
    }, []);

  const selectAll = async () => {
    try {
      const { data } = await axios.get('/api/places'); // 프록시 사용
      setPlaceList(Array.isArray(data) ? data : (data?.content ?? []));
    } catch (e) {
      console.error(e);
    }
  };

    // http://localhost:4000/List -> package.json - proxy
    // ES6 -> template string, arrow function, async-await


  return (
    <div className="App">
      <BrowserRouter>
        <Routes>
            <Route path='/' element={<PostMain placeList={placeList}/>}/>
            <Route path='/paris' element={<PostParis placeList={placeList}/>}/>
            <Route path="/paris/:id" element={<PostParis placeList={placeList} />} />
            <Route path='/london' element={<PostLondon placeList={placeList}/>}/>
            <Route path="/london/:id" element={<PostLondon placeList={placeList} />} />
            <Route path='/about' element={<PostAbout placeList={placeList}/>}/>
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;

