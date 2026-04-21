import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { createUser, getUser } from '../api';

export default function AuthPage({ mode, onLogin }) {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [birthday, setBirthday] = useState('');
  const [region, setRegion] = useState('');
  const [sex, setSex] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const signupDisabled = mode === 'signup' && (!region || !sex);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (mode === 'signup') {
        const demographics = {};
        if (region) demographics.region = region;
        if (sex) demographics.sex = sex;

        const res = await createUser({
          username,
          email,
          passwordHash: password,
          birthday: birthday || null,
          demographics,
        });
        onLogin(res);
        navigate('/');
      } else {
        const res = await getUser(username);
        onLogin(res);
        navigate('/');
      }
    } catch (err) {
      setError(err.message || 'Something went wrong');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-wrapper">
      <div className="auth-card">
        <h1>Global Ranking System</h1>
        <p className="subtitle">
          {mode === 'login' ? 'Log in to continue' : 'Create your account'}
        </p>

        {error && <div className="error-banner">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Username</label>
            <input
              className="input"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              placeholder="Enter username"
            />
          </div>

          {mode === 'signup' && (
            <>
              <div className="form-group">
                <label>Email</label>
                <input
                  className="input"
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                  placeholder="you@example.com"
                />
              </div>

              <div className="form-group">
                <label>Password</label>
                <input
                  className="input"
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                  placeholder="Choose a password"
                />
              </div>

              <div className="form-group">
                <label>Birthday</label>
                <input
                  className="input"
                  type="date"
                  value={birthday}
                  onChange={(e) => setBirthday(e.target.value)}
                />
              </div>

              <div className="form-group">
                <label>Region <span style={{color:'red'}}>*</span></label>
                <select className="input" value={region} onChange={(e) => setRegion(e.target.value)}>
                  <option value="">Select region</option>
                  <option value="North America">North America</option>
                  <option value="South America">South America</option>
                  <option value="Europe">Europe</option>
                  <option value="Africa">Africa</option>
                  <option value="Asia">Asia</option>
                  <option value="Oceania">Oceania</option>
                </select>
              </div>

              <div className="form-group">
                <label>Sex <span style={{color:'red'}}>*</span></label>
                <select className="input" value={sex} onChange={(e) => setSex(e.target.value)}>
                  <option value="">Select sex</option>
                  <option value="Male">Male</option>
                  <option value="Female">Female</option>
                  <option value="Other">Other</option>
                </select>
              </div>

              {signupDisabled && (
                <p style={{color:'red', fontSize:'0.85rem', marginBottom:'8px'}}>
                  Please select both region and sex to continue.
                </p>
              )}
            </>
          )}

          {mode === 'login' && (
            <div className="form-group">
              <label>Password</label>
              <input
                className="input"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter password"
              />
            </div>
          )}

          <button type="submit" className="btn btn-primary btn-full" disabled={loading || signupDisabled}>
            {loading ? 'Please wait...' : mode === 'login' ? 'Log in' : 'Create account'}
          </button>
        </form>

        <div className="auth-footer">
          {mode === 'login' ? (
            <p>
              Don&apos;t have an account? <Link to="/signup">Create one</Link>
            </p>
          ) : (
            <p>
              Already have an account? <Link to="/login">Log in</Link>
            </p>
          )}
        </div>
      </div>
    </div>
  );
}
