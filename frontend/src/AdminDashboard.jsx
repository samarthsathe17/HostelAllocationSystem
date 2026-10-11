import React, { useState, useEffect } from 'react';

export default function AdminDashboard() {
  const [activeTab, setActiveTab] = useState('requests');
  const [settings, setSettings] = useState({
    minCgpa: 6.0,
    minAttendance: 75.0,
    weightCgpa: 30,
    weightAttendance: 20,
    weightDistance: 30,
    weightFinancial: 10,
    weightYear: 10
  });
  const [requests, setRequests] = useState([]);

  useEffect(() => {
    fetchSettings();
    fetchRequests();
  }, []);

  const fetchSettings = () => {
    fetch('http://localhost:8080/api/settings')
      .then(res => res.json())
      .then(data => setSettings(data))
      .catch(err => console.error(err));
  };

  const fetchRequests = () => {
    fetch('http://localhost:8080/api/allocate/requests')
      .then(res => res.json())
      .then(data => setRequests(data))
      .catch(err => console.error(err));
  };

  const handleSettingChange = (e) => {
    setSettings({ ...settings, [e.target.name]: e.target.value });
  };

  const saveSettings = (e) => {
    e.preventDefault();
    fetch('http://localhost:8080/api/settings', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(settings)
    })
      .then(res => res.json())
      .then(data => alert('Settings saved!'))
      .catch(err => console.error(err));
  };

  const handleAction = (reqId, action, roomNo) => {
    if (action === 'APPROVE' && !roomNo) {
        alert("Cannot approve without a room.");
        return;
    }
    fetch('http://localhost:8080/api/allocate/confirm', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ requestId: reqId, action, roomNo: roomNo || '' })
    })
      .then(res => res.json())
      .then(data => {
        if (data.status === 'success') {
            alert(`Request ${action}D successfully`);
            fetchRequests();
        } else {
            alert('Error: ' + data.message);
        }
      })
      .catch(err => console.error(err));
  };

  return (
    <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '40px 20px', color: '#111827' }}>
      <h2 style={{ color: '#1f3c88', borderBottom: '2px solid #D97706', paddingBottom: '10px' }}>Admin Dashboard</h2>
      
      <div style={{ display: 'flex', gap: '20px', marginBottom: '30px' }}>
        <button 
          style={{ padding: '10px 20px', backgroundColor: activeTab === 'requests' ? '#1f3c88' : '#e5e7eb', color: activeTab === 'requests' ? '#fff' : '#000', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
          onClick={() => setActiveTab('requests')}
        >Pending Requests</button>
        <button 
          style={{ padding: '10px 20px', backgroundColor: activeTab === 'settings' ? '#1f3c88' : '#e5e7eb', color: activeTab === 'settings' ? '#fff' : '#000', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
          onClick={() => setActiveTab('settings')}
        >Rules & Settings</button>
      </div>

      {activeTab === 'requests' && (
        <div>
          <h3>Allocation Requests</h3>
          <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '20px' }}>
            <thead>
              <tr style={{ backgroundColor: '#f3f4f6', textAlign: 'left' }}>
                <th style={{ padding: '12px', border: '1px solid #e5e7eb' }}>ID</th>
                <th style={{ padding: '12px', border: '1px solid #e5e7eb' }}>Student</th>
                <th style={{ padding: '12px', border: '1px solid #e5e7eb' }}>Priority Score</th>
                <th style={{ padding: '12px', border: '1px solid #e5e7eb' }}>Eligibility Details</th>
                <th style={{ padding: '12px', border: '1px solid #e5e7eb' }}>Recommended Room</th>
                <th style={{ padding: '12px', border: '1px solid #e5e7eb' }}>Status</th>
                <th style={{ padding: '12px', border: '1px solid #e5e7eb' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {requests.map(req => (
                <tr key={req.requestId}>
                  <td style={{ padding: '12px', border: '1px solid #e5e7eb' }}>{req.requestId}</td>
                  <td style={{ padding: '12px', border: '1px solid #e5e7eb' }}>{req.studentName} ({req.studentRollNo})</td>
                  <td style={{ padding: '12px', border: '1px solid #e5e7eb' }}><strong>{req.priorityScore.toFixed(2)}</strong></td>
                  <td style={{ padding: '12px', border: '1px solid #e5e7eb', color: req.isEligible ? 'green' : 'red' }}>
                    {req.isEligible ? 'Eligible' : 'Not Eligible'} <br/>
                    <small>{req.eligibilityDetails}</small>
                  </td>
                  <td style={{ padding: '12px', border: '1px solid #e5e7eb' }}>{req.recommendedRoom || 'None'}</td>
                  <td style={{ padding: '12px', border: '1px solid #e5e7eb' }}>{req.status}</td>
                  <td style={{ padding: '12px', border: '1px solid #e5e7eb' }}>
                    {req.status === 'PENDING' && (
                      <div style={{ display: 'flex', gap: '10px' }}>
                        <button 
                            disabled={!req.recommendedRoom}
                            onClick={() => handleAction(req.requestId, 'APPROVE', req.recommendedRoom)}
                            style={{ backgroundColor: req.recommendedRoom ? '#10b981' : '#ccc', color: '#fff', border: 'none', padding: '6px 12px', borderRadius: '4px', cursor: req.recommendedRoom ? 'pointer' : 'not-allowed' }}>
                          Approve
                        </button>
                        <button 
                            onClick={() => handleAction(req.requestId, 'REJECT', null)}
                            style={{ backgroundColor: '#ef4444', color: '#fff', border: 'none', padding: '6px 12px', borderRadius: '4px', cursor: 'pointer' }}>
                          Reject
                        </button>
                      </div>
                    )}
                  </td>
                </tr>
              ))}
              {requests.length === 0 && <tr><td colSpan="7" style={{ padding: '20px', textAlign: 'center' }}>No requests found</td></tr>}
            </tbody>
          </table>
        </div>
      )}

      {activeTab === 'settings' && (
        <div style={{ backgroundColor: '#fff', padding: '30px', borderRadius: '8px', border: '1px solid #e5e7eb' }}>
          <h3>Eligibility Thresholds</h3>
          <form onSubmit={saveSettings}>
            <div style={{ display: 'flex', gap: '20px', marginBottom: '20px' }}>
              <div>
                <label style={{ display: 'block', marginBottom: '5px' }}>Min CGPA</label>
                <input type="number" step="0.1" name="minCgpa" value={settings.minCgpa} onChange={handleSettingChange} style={{ padding: '8px', border: '1px solid #ccc', borderRadius: '4px' }} />
              </div>
              <div>
                <label style={{ display: 'block', marginBottom: '5px' }}>Min Attendance (%)</label>
                <input type="number" step="0.1" name="minAttendance" value={settings.minAttendance} onChange={handleSettingChange} style={{ padding: '8px', border: '1px solid #ccc', borderRadius: '4px' }} />
              </div>
            </div>

            <h3 style={{ marginTop: '30px' }}>Priority Weights (Total usually 100)</h3>
            <div style={{ display: 'flex', gap: '20px', flexWrap: 'wrap', marginBottom: '30px' }}>
              <div>
                <label style={{ display: 'block', marginBottom: '5px' }}>CGPA Weight</label>
                <input type="number" name="weightCgpa" value={settings.weightCgpa} onChange={handleSettingChange} style={{ padding: '8px', border: '1px solid #ccc', borderRadius: '4px' }} />
              </div>
              <div>
                <label style={{ display: 'block', marginBottom: '5px' }}>Attendance Weight</label>
                <input type="number" name="weightAttendance" value={settings.weightAttendance} onChange={handleSettingChange} style={{ padding: '8px', border: '1px solid #ccc', borderRadius: '4px' }} />
              </div>
              <div>
                <label style={{ display: 'block', marginBottom: '5px' }}>Distance Weight</label>
                <input type="number" name="weightDistance" value={settings.weightDistance} onChange={handleSettingChange} style={{ padding: '8px', border: '1px solid #ccc', borderRadius: '4px' }} />
              </div>
              <div>
                <label style={{ display: 'block', marginBottom: '5px' }}>Financial Need Weight</label>
                <input type="number" name="weightFinancial" value={settings.weightFinancial} onChange={handleSettingChange} style={{ padding: '8px', border: '1px solid #ccc', borderRadius: '4px' }} />
              </div>
              <div>
                <label style={{ display: 'block', marginBottom: '5px' }}>Year of Study Weight</label>
                <input type="number" name="weightYear" value={settings.weightYear} onChange={handleSettingChange} style={{ padding: '8px', border: '1px solid #ccc', borderRadius: '4px' }} />
              </div>
            </div>

            <button type="submit" style={{ backgroundColor: '#A91D22', color: '#fff', border: 'none', padding: '10px 24px', borderRadius: '6px', fontSize: '1rem', cursor: 'pointer' }}>
              Save Settings
            </button>
          </form>
        </div>
      )}
    </div>
  );
}
