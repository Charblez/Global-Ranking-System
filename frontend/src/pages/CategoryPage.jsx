import { useState, useEffect, useCallback } from 'react';
import { useParams } from 'react-router-dom';
import { getCategory, getTopScores, getFilteredScores, getBaselines, submitScore } from '../api';

const MAX_SCORE = 999999999999;
const MIN_SCORE = 0;

const isValidScore = (val) => {
  if (val === '' || val === null || val === undefined) return false;
  if (/e/i.test(String(val))) return false;
  const num = parseFloat(val);
  if (isNaN(num)) return false;
  if (num < MIN_SCORE || num > MAX_SCORE) return false;
  return true;
};

export default function CategoryPage({ currentUser }) {
  const { categoryId } = useParams();
  const [category, setCategory] = useState(null);
  const [scores, setScores] = useState([]);
  const [baselines, setBaselines] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [filterRegion, setFilterRegion] = useState('');
  const [filterSex, setFilterSex] = useState('');
  const [filtersActive, setFiltersActive] = useState(false);

  const [scoreValue, setScoreValue] = useState('');
  const [anonymous, setAnonymous] = useState(false);
  const [submitError, setSubmitError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const loadCategory = useCallback(async () => {
    try {
      const cat = await getCategory(categoryId);
      setCategory(cat);
    } catch (err) {
      setError(err.message);
    }
  }, [categoryId]);

  const loadScores = useCallback(async (currentPage = 0) => {
    try {
      let data;
      const tags = {};
      if (filterRegion) tags.region = filterRegion;
      if (filterSex) tags.sex = filterSex;

      const hasFilters = Object.keys(tags).length > 0;
      setFiltersActive(hasFilters);

      if (hasFilters) {
        data = await getFilteredScores(categoryId, tags, currentPage, 25);
      } else {
        data = await getTopScores(categoryId, currentPage, 25);
      }

      const allScores = data.scores || [];
      const seen = new Set();
      const deduped = allScores.filter((entry) => {
        if (seen.has(entry.username)) return false;
        seen.add(entry.username);
        return true;
      });

      setScores(deduped);
      setPage(data.page);
      setTotalPages(data.totalPages);
      setTotalElements(data.totalElements);
    } catch (err) {
      setError(err.message);
    }
  }, [categoryId, filterRegion, filterSex]);

  const loadBaselines = useCallback(async () => {
    try {
      const data = await getBaselines(categoryId);
      setBaselines(data || []);
    } catch {
      // baselines are optional
    }
  }, [categoryId]);

  useEffect(() => {
    setLoading(true);
    Promise.all([loadCategory(), loadScores(0), loadBaselines()]).finally(() =>
      setLoading(false)
    );
  }, [loadCategory, loadScores, loadBaselines]);

  const handleFilter = () => {
    setPage(0);
    loadScores(0);
  };

  const clearFilters = () => {
    setFilterRegion('');
    setFilterSex('');
    setFiltersActive(false);
    setTimeout(() => loadScores(0), 0);
  };

  const handleScoreChange = (e) => {
    const val = e.target.value;
    if (val === '' || val === '-' || val === '.') {
      setScoreValue(val);
      return;
    }
    if (/e/i.test(val)) return;
    setScoreValue(val);
  };

  const handleSubmitScore = async (e) => {
    e.preventDefault();
    setSubmitError('');

    if (!isValidScore(scoreValue)) {
      setSubmitError('Score must be a plain number between 0 and 999,999,999,999. Scientific notation (e.g. 1e100) is not allowed.');
      return;
    }

    const num = parseFloat(scoreValue);
    setSubmitting(true);
    try {
      const tags = {};
      if (currentUser.demographics) {
        Object.assign(tags, currentUser.demographics);
      }

      await submitScore({
        user_id: currentUser.userId,
        category_id: categoryId,
        score: num,
        tags,
        anonymous,
      });
      setScoreValue('');
      await Promise.all([loadScores(page), loadBaselines()]);
    } catch (err) {
      setSubmitError(err.message || 'Failed to submit score');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <div className="page"><div className="loading">Loading...</div></div>;
  if (!category) return <div className="page"><div className="error-banner">{error || 'Category not found'}</div></div>;

  const baseline = baselines.length > 0 ? baselines[0] : null;

  const getRankLabel = (rank) => {
    if (rank === 1) return '1st';
    if (rank === 2) return '2nd';
    if (rank === 3) return '3rd';
    return `${rank}th`;
  };

  const getMedal = (rank) => {
    if (rank === 1) return '\u{1F947}';
    if (rank === 2) return '\u{1F948}';
    if (rank === 3) return '\u{1F949}';
    return '';
  };

  const formatNumber = (num) => {
    if (num == null) return '-';
    return Number(num).toLocaleString(undefined, { maximumFractionDigits: 2 });
  };

  return (
    <div className="page">
      <h1 className="page-title">{category.name}</h1>
      {category.description && <p style={{ color: 'var(--text-muted)', marginBottom: 24, marginTop: -16 }}>{category.description}</p>}

      {error && <div className="error-banner">{error}</div>}

      <div className="category-layout">
        <div className="leaderboard-section">
          <div className="panel" style={{ padding: 0, overflow: 'hidden' }}>
            <div style={{ padding: '16px 24px', borderBottom: '1px solid var(--border)' }}>
              <div className="panel-title" style={{ marginBottom: 0 }}>
                Leaderboard
                {filtersActive && <span className="tag" style={{ marginLeft: 8 }}>Filtered</span>}
              </div>
            </div>

            <div style={{ maxHeight: 600, overflowY: 'auto' }}>
              {scores.length === 0 ? (
                <div className="empty-state"><p>No scores submitted yet.</p></div>
              ) : (
                <table className="leaderboard-table">
                  <thead>
                    <tr>
                      <th>Rank</th>
                      <th>Player</th>
                      <th>Score ({category.units})</th>
                      <th>Date</th>
                    </tr>
                  </thead>
                  <tbody>
                    {scores.map((entry, idx) => (
                      <tr key={entry.scoreId}>
                        <td className="rank-cell">
                          <span className="rank-medal">{getMedal(idx + 1)}</span>{' '}
                          {getRankLabel(idx + 1)}
                        </td>
                        <td className={`name-cell ${entry.anonymous ? 'anonymous-name' : ''}`}>
                          {entry.username}
                        </td>
                        <td className="score-cell">{formatNumber(entry.score)}</td>
                        <td style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                          {entry.submittedAt ? new Date(entry.submittedAt).toLocaleDateString() : '-'}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>

            {totalPages > 1 && (
              <div className="pagination" style={{ padding: 12, borderTop: '1px solid var(--border)' }}>
                <button className="btn btn-ghost btn-sm" disabled={page === 0} onClick={() => { setPage(page - 1); loadScores(page - 1); }}>Prev</button>
                <span className="page-info">Page {page + 1} of {totalPages}</span>
                <button className="btn btn-ghost btn-sm" disabled={page >= totalPages - 1} onClick={() => { setPage(page + 1); loadScores(page + 1); }}>Next</button>
              </div>
            )}
          </div>
        </div>

        <div className="panel">
          <div className="panel-title">Statistics</div>
          {baseline ? (
            <div className="stats-grid">
              <div className="stat-item"><div className="stat-value">{formatNumber(baseline.mean)}</div><div className="stat-label">Mean</div></div>
              <div className="stat-item"><div className="stat-value">{formatNumber(baseline.standardDeviation)}</div><div className="stat-label">Std Dev</div></div>
              <div className="stat-item"><div className="stat-value">{baseline.sampleSize ?? '-'}</div><div className="stat-label">Participants</div></div>
              <div className="stat-item"><div className="stat-value">{totalElements}</div><div className="stat-label">Total Entries</div></div>
              {baseline.median != null && <div className="stat-item"><div className="stat-value">{formatNumber(baseline.median)}</div><div className="stat-label">Median</div></div>}
              <div className="stat-item"><div className="stat-value">{category.units}</div><div className="stat-label">Unit</div></div>
            </div>
          ) : (
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>No baseline statistics available yet.</p>
          )}
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
          <div className="panel">
            <div className="panel-title">Filters</div>
            <div className="filter-row">
              <div className="filter-group">
                <span className="filter-label">Region</span>
                <select className="input" value={filterRegion} onChange={(e) => setFilterRegion(e.target.value)}>
                  <option value="">All Regions</option>
                  <option value="North America">North America</option>
                  <option value="South America">South America</option>
                  <option value="Europe">Europe</option>
                  <option value="Africa">Africa</option>
                  <option value="Asia">Asia</option>
                  <option value="Oceania">Oceania</option>
                </select>
              </div>
              <div className="filter-group">
                <span className="filter-label">Sex</span>
                <select className="input" value={filterSex} onChange={(e) => setFilterSex(e.target.value)}>
                  <option value="">All</option>
                  <option value="Male">Male</option>
                  <option value="Female">Female</option>
                  <option value="Other">Other</option>
                </select>
              </div>
            </div>
            <div style={{ display: 'flex', gap: 8 }}>
              <button className="btn btn-primary btn-sm" onClick={handleFilter}>Apply Filters</button>
              {filtersActive && <button className="btn btn-ghost btn-sm" onClick={clearFilters}>Clear</button>}
            </div>
          </div>

          <div className="panel">
            <div className="panel-title">Submit Score</div>
            {currentUser ? (
              <form className="submit-form" onSubmit={handleSubmitScore}>
                {submitError && <div className="error-banner">{submitError}</div>}
                <div className="score-input-row">
                  <input
                    className="input"
                    type="number"
                    step="any"
                    min={MIN_SCORE}
                    max={MAX_SCORE}
                    value={scoreValue}
                    onChange={handleScoreChange}
                    placeholder="0"
                    required
                  />
                  <span className="unit-label">{category.units}</span>
                </div>
                <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', margin: '4px 0 8px' }}>
                  Valid range: 0 – 999,999,999,999
                </p>
                <label style={{ display: 'flex', alignItems: 'center', gap: 8, fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                  <input type="checkbox" checked={anonymous} onChange={(e) => setAnonymous(e.target.checked)} />
                  Submit anonymously
                </label>
                <button className="btn btn-primary btn-full" type="submit" disabled={submitting}>
                  {submitting ? 'Submitting...' : 'Submit'}
                </button>
              </form>
            ) : (
              <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Log in to submit a score.</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
