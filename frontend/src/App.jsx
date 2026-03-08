import React, { useState } from 'react';

// --- SHARED STYLES ---
const pageWrapperStyle = {
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  justifyContent: 'center', // Centers vertically
  minHeight: '100vh',       // Full viewport height
  width: '100vw',
  fontFamily: 'sans-serif',
  textAlign: 'center',
  gap: '20px'
};

const tableContainerStyle = {
  maxHeight: '400px',
  overflowY: 'auto',        // Allows scrolling if list > 100
  marginTop: '20px',
  border: '1px solid #ddd',
  borderRadius: '8px'
};

const buttonStyle = {
  padding: '12px 24px',
  fontSize: '1rem',
  cursor: 'pointer',
  borderRadius: '5px',
  border: '1px solid #8b5cf6',
  backgroundColor: '#fff',
  transition: '0.2s'
};

// --- COMPONENTS ---

// 1. The Dynamic Ranking Table Page
const RankingPage = ({ title, setPage }) => {
  const [entries, setEntries] = useState([]);
  const [name, setName] = useState('');
  const [val, setVal] = useState('');

  const addEntry = (e) => {
    e.preventDefault();
    if (!name || !val) return;
    const newEntry = { name, value: parseFloat(val) };
    const updated = [...entries, newEntry]
      .sort((a, b) => b.value - a.value)
      .slice(0, 100);
    setEntries(updated);
    setName('');
    setVal('');
  };

  return (
    <div style={pageWrapperStyle}>
      <h1>{title} Leaderboard 🏆</h1>
      <form onSubmit={addEntry} style={{ display: 'flex', gap: '10px' }}>
        <input placeholder="Name" value={name} onChange={e => setName(e.target.value)} style={{ padding: '8px' }} />
        <input type="number" placeholder="Score" value={val} onChange={e => setVal(e.target.value)} style={{ padding: '8px' }} />
        <button type="submit" style={buttonStyle}>Submit</button>
      </form>

      <div style={tableContainerStyle}>
        <table style={{ width: '400px', borderCollapse: 'collapse' }}>
          <thead style={{ backgroundColor: '#f3f4f6' }}>
            <tr>
              <th style={{ padding: '10px' }}>Rank</th>
              <th>Name</th>
              <th>Score</th>
            </tr>
          </thead>
          <tbody>
            {entries.map((item, i) => (
              <tr key={i} style={{ borderBottom: '1px solid #eee' }}>
                <td style={{ padding: '10px' }}>{i + 1}</td>
                <td>{item.name}</td>
                <td>{item.value}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <button onClick={() => setPage('home')} style={{ ...buttonStyle, backgroundColor: '#eee' }}>Back</button>
    </div>
  );
};

// 2. The Global Categories Menu
const GlobalMenu = ({ setPage, setSelectedCategory }) => {
  const categories = ["Wealth", "Health", "Speed", "Intelligence"];
  
  return (
    <div style={pageWrapperStyle}>
      <h1>Global Categories</h1>
      <p>Select a category to view the rankings:</p>
      <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
        {categories.map(cat => (
          <button 
            key={cat} 
            style={buttonStyle} 
            onClick={() => {
              setSelectedCategory(cat);
              setPage('RankingPage');
            }}
          >
            {cat} Rankings
          </button>
        ))}
      </div>
      <button onClick={() => setPage('home')} style={{ marginTop: '20px' }}>Back to Home</button>
    </div>
  );
};

// 3. The Home Page
const HomePage = ({ setPage }) => (
  <div style={pageWrapperStyle}>
    <h1>Global Ranking System</h1>
    <div style={{ display: 'flex', gap: '20px' }}>
      <button style={buttonStyle} onClick={() => setPage('GlobalMenu')}>Global Data</button>
      <button style={buttonStyle} onClick={() => setPage('LocalRankingPage')}>Local Data</button>
    </div>
  </div>
);

// --- MAIN APP ---
const App = () => {
  const [currentPage, setCurrentPage] = useState('home');
  const [selectedCategory, setSelectedCategory] = useState('');

  const renderPage = () => {
    switch (currentPage) {
      case 'home': 
        return <HomePage setPage={setCurrentPage} />;
      case 'GlobalMenu': 
        return <GlobalMenu setPage={setCurrentPage} setSelectedCategory={setSelectedCategory} />;
      case 'RankingPage': 
        return <RankingPage title={selectedCategory} setPage={setCurrentPage} />;
      case 'LocalRankingPage': 
        return <RankingPage title="Local Data" setPage={setCurrentPage} />;
      default: 
        return <HomePage setPage={setCurrentPage} />;
    }
  };

  return <div>{renderPage()}</div>;
};

export default App;