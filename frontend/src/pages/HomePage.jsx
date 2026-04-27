import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getCategories } from '../api';

export default function HomePage() {
  const [categories, setCategories] = useState(() => {
    const cached = sessionStorage.getItem('categoriesCache');
    return cached ? JSON.parse(cached) : [];
  });
  const [loading, setLoading] = useState(() => !sessionStorage.getItem('categoriesCache'));
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    getCategories(0, 100)
      .then((data) => {
        const list = data.categories || [];
        setCategories(list);
        sessionStorage.setItem('categoriesCache', JSON.stringify(list));
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="page"><div className="loading">Loading categories...</div></div>;

  return (
    <div className="page">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
        <h1 className="page-title" style={{ marginBottom: 0 }}>Categories</h1>
        <Link to="/create">
          <button className="btn btn-primary">+ New Category</button>
        </Link>
      </div>

      {error && <div className="error-banner">{error}</div>}

      {categories.length === 0 ? (
        <div className="empty-state">
          <h3>No categories yet</h3>
          <p>Check back later for ranking categories.</p>
        </div>
      ) : (
        <div className="card-grid">
          {categories.map((cat) => (
            <div
              key={cat.categoryId}
              className="category-card"
              onClick={() => navigate(`/category/${cat.categoryId}`)}
            >
              <h3>{cat.name}</h3>
              {cat.description && (
                <p className="card-meta">{cat.description}</p>
              )}
              <p className="card-meta">
                Unit: {cat.units} &middot; {cat.sortOrder ? 'Higher is better' : 'Lower is better'}
              </p>
              {cat.tags && cat.tags.length > 0 && (
                <div className="card-tags">
                  {cat.tags.map((t) => (
                    <span key={t} className="tag">{t}</span>
                  ))}
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
