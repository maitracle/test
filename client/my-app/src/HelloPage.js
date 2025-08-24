import React, { useState, useEffect } from 'react';

function HelloPage() {
  const [apiData, setApiData] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchData = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await fetch('http://localhost:8080/hello');
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.text();
      setApiData(data);
    } catch (err) {
      setError(`API 호출 중 오류가 발생했습니다: ${err.message}`);
      console.error('API 호출 오류:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    // 페이지 로드 시 자동으로 API 호출
    fetchData();
  }, []);

  return (
    <div className="hello-page">
      <h1>Hello 페이지</h1>
      <p>이것은 /hello 경로의 Hello 페이지입니다.</p>
      
      <div className="api-section">
        <h2>API 테스트</h2>
        <p>localhost:8080/hello에서 데이터를 가져옵니다</p>
        
        <button 
          onClick={fetchData} 
          disabled={loading}
          className="fetch-button"
        >
          {loading ? '로딩 중...' : '데이터 새로고침'}
        </button>

        {loading && (
          <div className="loading">
            <p>데이터를 가져오는 중...</p>
          </div>
        )}

        {error && (
          <div className="error">
            <p>오류: {error}</p>
          </div>
        )}

        {apiData && !loading && !error && (
          <div className="api-result">
            <h3>받은 데이터:</h3>
            <div className="data-display">
              {apiData}
            </div>
          </div>
        )}
      </div>

      <div className="hello-content">
        <h2>Hello 내용</h2>
        <ul>
          <li>첫 번째 Hello 항목</li>
          <li>두 번째 Hello 항목</li>
          <li>세 번째 Hello 항목</li>
        </ul>
      </div>
    </div>
  );
}

export default HelloPage; 