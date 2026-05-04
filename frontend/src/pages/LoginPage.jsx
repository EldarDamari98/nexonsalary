import { useState } from "react";

// Hardcoded credentials — in a real production app these would be validated server-side
const VALID_USERNAME = "admin";
const VALID_PASSWORD = "admin123";

/**
 * The login screen shown to users before they can access the app.
 *
 * Displays a centered card with username and password fields.
 * On successful login it calls onLogin() which updates the app-level state
 * and grants access to the rest of the application.
 *
 * @param {Function} onLogin - callback to call when the user successfully logs in
 */
function LoginPage({ onLogin }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  // Holds the error message shown below the form when login fails
  const [error, setError] = useState("");

  /**
   * Handles form submission — checks the entered credentials against the hardcoded values.
   * If correct, calls onLogin(). If wrong, shows an error message.
   *
   * @param {React.FormEvent} e - the form submit event (prevented to avoid page reload)
   */
  function handleSubmit(e) {
    e.preventDefault(); // Prevent the browser from reloading the page
    if (username === VALID_USERNAME && password === VALID_PASSWORD) {
      onLogin();
    } else {
      setError("Invalid username or password");
    }
  }

  return (
    <div className="login-page">
      <div className="login-card">
        {/* Logo — matches the sidebar style */}
        <h1 className="login-logo">NexonSalary</h1>
        <p className="login-subtitle">Sign in to your account</p>

        <form onSubmit={handleSubmit} className="login-form">
          <div className="login-field">
            <label>Username</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Enter username"
              autoFocus
            />
          </div>

          <div className="login-field">
            <label>Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter password"
            />
          </div>

          {/* Only shown when login fails */}
          {error && <p className="login-error">{error}</p>}

          <button type="submit" className="login-btn">Sign In</button>
        </form>
      </div>
    </div>
  );
}

export default LoginPage;
