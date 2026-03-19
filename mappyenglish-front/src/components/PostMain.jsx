// import '../css/PostMain.css';
// import PostList from './Main/PostList';
// import MyComponent from './Main/Post';
import Header from './Main/Header';
import BottomBar from './Main/BottomBar'; // 파일이름은 무조건 대문자로!

import { GoogleMap, Marker, useJsApiLoader } from '@react-google-maps/api';


const apiKey = process.env.REACT_APP_GMAPS_KEY; // ✅ CRA 방식
if (!apiKey) {
  // 런타임 가드(선택)
  throw new Error('REACT_APP_GMAPS_KEY 가 .env에 설정되지 않았습니다.');
}
const GMAPS_LIBRARIES = ['places'];

function PostMain({placeList = []}){ // 기본값: []

  const { isLoaded, loadError } = useJsApiLoader({
    id: 'google-map',
    googleMapsApiKey: apiKey,
    libraries: GMAPS_LIBRARIES, // libraries: ['places'] 코드는
    // 랜더때마다 ['places']를 새로 만들어 넘기면 스크립트를 다시 로드하려고 해서
    // 경고가 남.
  });

  if (loadError) return <div>지도를 불러오는 중 오류가 발생했습니다.</div>;
  if (!isLoaded) return <div>지도 로딩 중…</div>;

    return(
        <div id='post-main'>
            <Header/>
        <GoogleMap
          mapContainerStyle={{ width: '100%', height: '60vh' }}
          center={{ lat: 49.389, lng: 7.584 }}
          zoom={4.5}
        >
        {placeList.map(p => (<Marker key={p.id} position={{lat:p.lat, lng:p.lng}} title={p.name}/>))}
        </GoogleMap>
        <BottomBar/>
            {/* <PostList postList={props.postList}/> */}
        </div>
    )
}

export default PostMain;