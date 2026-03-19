import '../css/PostParis.css';
import BottomBar from './Main/BottomBar'; // 파일이름은 무조건 대문자로!
import BottomSheet from './Main/BottomSheet';
import BottomSection from './Main/BottomSection';
import { useState, useCallback, useRef, useEffect, useMemo } from 'react';
import axios from 'axios';
import { useNavigate, useParams } from 'react-router-dom';

import { GoogleMap, Marker, useJsApiLoader } from '@react-google-maps/api';


const apiKey = process.env.REACT_APP_GMAPS_KEY; // ✅ CRA 방식
if (!apiKey) {
  // 런타임 가드(선택)
  throw new Error('REACT_APP_GMAPS_KEY 가 .env에 설정되지 않았습니다.');
}
const GMAPS_LIBRARIES = ['places'];

const CATEGORIES = [
  { code: 'ALL', label: '모든 장소', className: 'place' },
  { code: 'A',   label: '관광명소', className: 'tour' },
  { code: 'B',   label: '음식점',   className: 'restaurant' },
  { code: 'C',   label: '상점',     className: 'store' },
  { code: 'D',   label: '대중교통', className: 'traffic' },
  { code: 'E',   label: '기타 시설', className: 'others' },
];

function PostLondon({ placeList }){

    const navigate = useNavigate();
    const { id } = useParams(); // /paris 또는 /paris/:id 모두 대응
    const hasId = Boolean(id);

    const mapRef = useRef(null);
    const defaultCenter = useMemo(() => ({ lat: 51.507, lng: -0.127 }), []);
    const mapOptions = useMemo(
        () => ({
          clickableIcons: false,
          gestureHandling: 'greedy',
          mapTypeControl: false,
          streetViewControl: false,
          fullscreenControl: false,
          zoomControl: true,
        }),
        []
      );

    const [open, setOpen] = useState(false);
    const [selectedPlace, setSelectedPlace] = useState(null);

    const [category, setCategory] = useState('ALL');

    // 대화 데이터
    const [conversations, setConversations] = useState([]);
    const [convLoading, setConvLoading] = useState(false);
    const [convError, setConvError] = useState(null);

    const { isLoaded, loadError } = useJsApiLoader({
      id: 'google-map',
      googleMapsApiKey: apiKey,
      libraries: GMAPS_LIBRARIES, // libraries: ['places'] 코드는
      // 랜더때마다 ['places']를 새로 만들어 넘기면 스크립트를 다시 로드하려고 해서
      // 경고가 남.
    });

    const onMapLoad = useCallback((map) => {
      mapRef.current = map;
    }, []);

    const panTo = useCallback((lat, lng, zoom = 15) => {
      if (!mapRef.current) return;
      mapRef.current.panTo({ lat, lng });
      mapRef.current.setZoom(zoom);
    }, []);

    // 마커 클릭: 선택 + 중심 이동 + 라우팅
    const handleMarkerClick = useCallback((p) => {
      setSelectedPlace(p);
      panTo(p.lat, p.lng);
      navigate(`/london/${p.id}`);
    }, [navigate, panTo]);

    // 지도 빈곳 클릭: 선택 해제 + 라우팅 원복 + 데이터 초기화
    const handleMapClick = useCallback(() => {
      setSelectedPlace(null);
      setConversations([]);
      setConvError(null);
      navigate('/london');
    }, [navigate]);

    // 수정
    const londonPlaceList = useMemo(() => {
          // city_id가 숫자 3이라고 가정
          return (placeList || []).filter(p => p.cityId === 3);
    }, [placeList]);

    // 수정
    // 현재 카테고리에 맞는 장소만 계산
    const filteredPlaces = useMemo(() => {
      if (category === 'ALL') return londonPlaceList;
      return londonPlaceList.filter((p) => String(p.category) === category);
    }, [londonPlaceList, category]);

    // 카테고리 바뀔 때 선택 장소/URL 정리 (필터에서 빠지면 해제)
    useEffect(() => {
      if (selectedPlace && category !== 'ALL' && String(selectedPlace.category) !== category) {
        setSelectedPlace(null);
        navigate('/london');
      }
    }, [category, selectedPlace, navigate]);

    // 수정
    // URL 파라미터(id) 변경 시: 해당 place를 찾아 중심 이동 + 대화 fetch
    useEffect(() => {
      if (!id) return;
      const placeId = Number(id);

      const p = londonPlaceList.find((x) => Number(x.id) === placeId);
      if (p) {
        setSelectedPlace(p);
        panTo(p.lat, p.lng);
        // URL로 직접 진입했을 때도 카테고리 자동 동기화(선택)
        if (p.category && String(p.category) !== category) {
          setCategory(String(p.category));
        }
      }

      // 대화 fetch (프록시 사용: /api/...)
      const fetchConversations = async () => {
        setConvLoading(true);
        setConvError(null);
        try {
          const { data } = await axios.get(`/api/conversations/place/${placeId}`);
          const list = Array.isArray(data) ? data : (data?.content ?? []);
          setConversations(list);
        } catch (e) {
          setConversations([]);
          setConvError(e?.message || String(e));
        } finally {
          setConvLoading(false);
        }
      };
      fetchConversations();
    }, [id, londonPlaceList, panTo, category]); // 수정

    if (loadError) return <div>지도를 불러오는 중 오류가 발생했습니다.</div>;
    if (!isLoaded) return <div>지도 로딩 중…</div>;

    // 버튼 공통 렌더러(활성 스타일 토글)
    const Chip = ({ value, label, className }) => (
      <button
        className={`chip sm ${className ?? ''} ${category === value ? 'active' : ''}`}
        onClick={() => setCategory(value)}
        aria-pressed={category === value}
        type="button"
      >
        {label}
      </button>
    );



    return(
        <div id='post-paris'>
            <div className="vh-screen" style={{minHeight:'100svh', display:'flex', flexDirection:'column'}}>
                {/* 상단 */}
                <header className='mappyHeader'>
                    <div className="safe-padded" style={{paddingTop: 'max(var(--space-4), env(safe-area-inset-top))'}}>
                        <div className="logo" style={{display:'flex', alignItems:'center', gap:'12px', padding:'12px 0'}}>
                            <img src="/MappyLogo.png"alt="Mappy English Logo"
                                style={{ height: '100px', margin:'-30px 0 -30px 0' }} // 크기는 원하는 대로 조절
                            />
                        </div>
                    </div>
                </header>
                {/* 본문 */}
                <main style={{ flex: 1, overflowY: 'auto', paddingBottom:
                    'calc(64px + max(var(--space-4), env(safe-area-inset-bottom)))' }}>
                    <div className="safe-padded" style={{flex:1}}>
                        <section className='main-container'>
                            <div className='category-bar'>
                                <div className="buttons">
                                  {CATEGORIES.map((c) => (
                                       <Chip key={c.code} value={c.code} label={c.label} className={c.className} />
                                  ))}
                                </div>
                            </div>
                        </section>

                        <GoogleMap
                            onLoad={onMapLoad}
                            onClick={handleMapClick}
                            options={mapOptions}
                            mapContainerStyle={{ width: '100%', height: '60vh' }}
                            {...(!hasId ? { center: defaultCenter } : { defaultCenter })}
                            zoom={12.8}
                        >
                        {filteredPlaces.map(p => (
                            <Marker key={p.id} position={{lat:p.lat, lng:p.lng}}
                            onClick={() => handleMarkerClick(p)} title={p.name}
                            />
                            ))}
                        </GoogleMap>
                        <BottomSection
                            conversations={conversations}
                            selectedPlace={selectedPlace}
                            open={open}
                            onOpen={() => setOpen(true)}
                            onClose={() => setOpen(false)}
                            title="런던의 대표장소"
                            peekHeight='32vh'   // 닫혀 있어도 카드 상단이 넉넉히 보이도록
                            halfHeight = '50vh'
                            fullHeight = '90vh'
                        >
                        </BottomSection>
                    </div>
                </main>

                {/* 하단 네비 (노치/홈바 안전) */}
                <BottomBar/>
            </div>
        </div>
    )
}

// [수정] 프롭 기본값 설정 (컴포넌트 밖이나 내부에서 구조분해할당 시)
PostLondon.defaultProps = {
  placeList: []
};

export default PostLondon;