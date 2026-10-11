import React, { useState, useEffect } from 'react';
import despuLogo from './assets/real_despu_logo.png';
import './index.css';

function App() {
  const [activeTab, setActiveTab] = useState('allocation');
  const [rooms, setRooms] = useState([]);
  const [formData, setFormData] = useState({
    name: '',
    rollNo: '',
    email: '',
    phone: '',
    cgpa: '',
    attendance: '',
    yearOfStudy: '1',
    distanceFromCampus: '',
    financialNeed: 'false',
    medicalNeeds: '',
    conductStatus: 'GOOD',
    preferredRoomType: 'SHARED',
    messPlan: 'STANDARD'
  });
  const [status, setStatus] = useState('');
  const [statusType, setStatusType] = useState('');
  const [loadingRooms, setLoadingRooms] = useState(true);

  useEffect(() => {
    fetch('http://localhost:8080/api/rooms/available')
      .then(res => res.json())
      .then(data => {
        setRooms(data);
        if (data.length > 0) {
          setFormData(prev => ({ ...prev, roomNo: data[0].split(" ")[1] }));
        }
        setLoadingRooms(false);
      })
      .catch(err => {
        console.error("Failed to fetch rooms:", err);
        const fallbackRooms = ["Room 101 (SINGLE)", "Room 102 (SHARED)"];
        setRooms(fallbackRooms);
        setFormData(prev => ({ ...prev, roomNo: fallbackRooms[0].split(" ")[1] }));
        setLoadingRooms(false);
      });
  }, []);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({ 
        ...prev, 
        [name]: type === 'checkbox' ? checked : value 
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!formData.email.includes('@') || !formData.email.includes('.')) {
      setStatus('Please enter a valid email address.');
      setStatusType('error');
      return;
    }

    setStatus('Processing allocation...');
    setStatusType('info');

    fetch('http://localhost:8080/api/allocate', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(formData)
    })
      .then(res => res.json())
      .then(data => {
        if (data.status === 'success') {
          setStatus('Application submitted successfully! Your priority score is being calculated.');
          setStatusType('success');
          setFormData(prev => ({ ...prev, name: '', rollNo: '', email: '', phone: '', cgpa: '', attendance: '', distanceFromCampus: '', medicalNeeds: '' }));
        } else {
          setStatus('Submission Failed: ' + data.message);
          setStatusType('error');
        }
      })
      .catch(err => {
        setStatus('Connection error. Is the server running?');
        setStatusType('error');
      });
  };

  return (
    <div className="app-container">
      {/* 1. Navigation & Header Bar */}
      <header className="brand-header">
        <div className="header-left">
          <img src={despuLogo} alt="DESPU Logo" className="brand-logo" style={{ width: '50px', height: '50px', borderRadius: '50%', marginRight: '15px' }} />
          <div className="brand-text">
            <span className="brand-top">DECCAN EDUCATION SOCIETY - Since 1884</span>
            <h1 className="brand-main">DES PUNE UNIVERSITY</h1>
          </div>
          <div className="legacy-badge">141 Years of Legacy</div>
        </div>

        <nav className="header-nav">
          <a href="#" onClick={(e) => { e.preventDefault(); setActiveTab('home'); }} className={activeTab === 'home' ? 'active' : ''}>Home</a>
          <a href="#" onClick={(e) => { e.preventDefault(); setActiveTab('allocation'); }} className={activeTab === 'allocation' ? 'active' : ''}>Apply for Hostel</a>
          <a href="#" onClick={(e) => { e.preventDefault(); setActiveTab('matrix'); }} className={activeTab === 'matrix' ? 'active' : ''}>Room Matrix</a>
          <a href="#" onClick={(e) => { e.preventDefault(); setActiveTab('guidelines'); }} className={activeTab === 'guidelines' ? 'active' : ''}>Guidelines</a>
          <a href="#" onClick={(e) => { e.preventDefault(); setActiveTab('admin'); }} className={activeTab === 'admin' ? 'active' : ''}>Admin</a>
        </nav>

        <div className="header-right">
          <button className="pill-btn announcement-btn">
            <span className="icon">📢</span> Announcements
          </button>
          <button className="cta-btn apply-btn">Apply Now</button>
          <div className="vertical-ribbon">Admission / Hostel Enquiry</div>
        </div>
      </header>

      {/* 2. Hero Section */}
      <section className="hero-section">
        <div className="hero-overlay"></div>
        <div className="hero-content">
          <div className="breadcrumb">Home &gt; Campus Life &gt; {activeTab === 'matrix' ? 'Room Matrix' : activeTab === 'guidelines' ? 'Guidelines & Regulations' : activeTab === 'home' ? 'Welcome' : 'Hostel & Mess Allocation'}</div>
          <h2 className="hero-title">{activeTab === 'matrix' ? 'Room Matrix' : activeTab === 'guidelines' ? 'Hostel & Mess Code of Conduct' : activeTab === 'home' ? 'Welcome to DESPU Campus Living' : 'In Pursuit of Knowledge'}</h2>
          <p className="hero-subtitle">{activeTab === 'matrix' ? 'View real-time room availability' : activeTab === 'guidelines' ? 'Official residential guidelines, dining schedules, and institutional policies for DES Pune University.' : activeTab === 'home' ? 'Modern, secure, and collaborative residential accommodation for DES Pune University students.' : 'Become a part of the 141 Year Educational Legacy of DES'}</p>
          {activeTab === 'home' && (
            <div style={{ marginTop: '30px', display: 'flex', gap: '20px', justifyContent: 'center' }}>
              <button onClick={() => setActiveTab('allocation')} style={{ backgroundColor: '#A91D22', color: 'white', padding: '12px 28px', border: 'none', borderRadius: '6px', fontSize: '1.05rem', fontWeight: 'bold', cursor: 'pointer', transition: 'background-color 0.2s', boxShadow: '0 4px 6px rgba(0,0,0,0.1)' }} onMouseEnter={(e) => e.target.style.backgroundColor = '#8c151a'} onMouseLeave={(e) => e.target.style.backgroundColor = '#A91D22'}>Apply for Room Allotment</button>
              <button onClick={() => setActiveTab('matrix')} style={{ backgroundColor: 'rgba(0,0,0,0.5)', color: 'white', padding: '12px 28px', border: '2px solid #D97706', borderRadius: '6px', fontSize: '1.05rem', fontWeight: 'bold', cursor: 'pointer', transition: 'all 0.2s' }} onMouseEnter={(e) => e.target.style.backgroundColor = 'rgba(217, 119, 6, 0.2)'} onMouseLeave={(e) => e.target.style.backgroundColor = 'rgba(0,0,0,0.5)'}>View Vacancy Matrix</button>
            </div>
          )}
        </div>
      </section>

      {activeTab === 'allocation' && (
        <>
          {/* 3. Step-by-Step Allocation Flow */}
          <section className="workflow-section">
        <div className="workflow-container">
          <div className="step">
            <div className="step-number">01</div>
            <div className="step-text">Enter Student Profile</div>
          </div>
          <div className="step-divider"></div>
          <div className="step">
            <div className="step-number">02</div>
            <div className="step-text">Fill Preferences & Criteria</div>
          </div>
          <div className="step-divider"></div>
          <div className="step">
            <div className="step-number">03</div>
            <div className="step-text">Auto-calculation of Priority Score</div>
          </div>
          <div className="step-divider"></div>
          <div className="step">
            <div className="step-number">04</div>
            <div className="step-text">Admin Review & Allocation Confirmation</div>
          </div>
        </div>
      </section>

      {/* 4. Centered Onboarding & Allocation Portal Card */}
      <main className="main-content">
        <div className="watermark-bg"></div>
        <div className="form-card">
          <div className="card-header">
            <h2>Hostel & Mess Allocation Portal</h2>
            <div className="gold-underline"></div>
          </div>

          {status && (
            <div className={`status-banner ${statusType}`}>
              {statusType === 'success' && <span className="status-icon">✓</span>}
              {statusType === 'error' && <span className="status-icon">⚠</span>}
              {statusType === 'info' && <span className="status-icon">ℹ</span>}
              <span>{status}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="onboarding-form">
            <div className="form-grid">
              <div className="input-group">
                <label>Full Name</label>
                <input type="text" name="name" value={formData.name} onChange={handleChange} required />
              </div>
              <div className="input-group">
                <label>Roll Number</label>
                <input type="text" name="rollNo" value={formData.rollNo} onChange={handleChange} required />
              </div>
              <div className="input-group">
                <label>Email Address</label>
                <input type="email" name="email" value={formData.email} onChange={handleChange} required />
              </div>
              <div className="input-group">
                <label>Phone Number</label>
                <input type="tel" name="phone" value={formData.phone} onChange={handleChange} required />
              </div>
            </div>

            <h3 style={{ marginTop: '20px', marginBottom: '10px', fontSize: '1.2rem', color: '#1f3c88' }}>Academic & Evaluation Criteria</h3>
            <div className="form-grid">
              <div className="input-group">
                <label>CGPA</label>
                <input type="number" step="0.1" name="cgpa" value={formData.cgpa} onChange={handleChange} required />
              </div>
              <div className="input-group">
                <label>Attendance (%)</label>
                <input type="number" step="0.1" name="attendance" value={formData.attendance} onChange={handleChange} required />
              </div>
              <div className="input-group">
                <label>Year of Study</label>
                <select name="yearOfStudy" value={formData.yearOfStudy} onChange={handleChange} className="custom-select" required>
                    <option value="1">1st Year</option>
                    <option value="2">2nd Year</option>
                    <option value="3">3rd Year</option>
                    <option value="4">4th Year</option>
                </select>
              </div>
              <div className="input-group">
                <label>Distance from Campus (km)</label>
                <input type="number" name="distanceFromCampus" value={formData.distanceFromCampus} onChange={handleChange} required />
              </div>
            </div>

            <h3 style={{ marginTop: '20px', marginBottom: '10px', fontSize: '1.2rem', color: '#1f3c88' }}>Additional Information</h3>
            <div className="form-grid">
              <div className="input-group">
                <label>Medical Needs (optional)</label>
                <input type="text" name="medicalNeeds" value={formData.medicalNeeds} onChange={handleChange} />
              </div>
              <div className="input-group">
                <label>Conduct Status</label>
                <select name="conductStatus" value={formData.conductStatus} onChange={handleChange} className="custom-select">
                    <option value="GOOD">Good Standing</option>
                    <option value="WARNING">Warning Issued</option>
                    <option value="SUSPENDED">Suspended</option>
                </select>
              </div>
            </div>

            <div style={{ marginTop: '15px' }}>
                <label style={{ display: 'flex', alignItems: 'center', gap: '10px', cursor: 'pointer' }}>
                    <input type="checkbox" name="financialNeed" checked={formData.financialNeed} onChange={handleChange} style={{ width: 'auto', height: 'auto' }} />
                    Applying for Financial Hardship / Scholarship Priority
                </label>
            </div>

            <h3 style={{ marginTop: '20px', marginBottom: '10px', fontSize: '1.2rem', color: '#1f3c88' }}>Preferences</h3>
            <div className="form-grid" style={{ gridTemplateColumns: '1fr' }}>
                <div className="input-group">
                    <label>Preferred Room Type</label>
                    <select name="preferredRoomType" value={formData.preferredRoomType} onChange={handleChange} className="custom-select">
                        <option value="SHARED">Shared (Standard)</option>
                        <option value="SINGLE">Single Occupancy</option>
                    </select>
                </div>
            </div>

            <div className="mess-plan-section mt-4">
              <label>Choose Mess Plan</label>
              <div className="pill-group">
                <label className={`radio-pill ${formData.messPlan === 'STANDARD' ? 'active' : ''}`}>
                  <input type="radio" name="messPlan" value="STANDARD" checked={formData.messPlan === 'STANDARD'} onChange={handleChange} className="hidden-radio" />
                  Standard Plan
                </label>
                <label className={`radio-pill ${formData.messPlan === 'PREMIUM' ? 'active' : ''}`}>
                  <input type="radio" name="messPlan" value="PREMIUM" checked={formData.messPlan === 'PREMIUM'} onChange={handleChange} className="hidden-radio" />
                  Premium Plan
                </label>
                <label className={`radio-pill ${formData.messPlan === 'VEG_ONLY' ? 'active' : ''}`}>
                  <input type="radio" name="messPlan" value="VEG_ONLY" checked={formData.messPlan === 'VEG_ONLY'} onChange={handleChange} className="hidden-radio" />
                  Veg Only Plan
                </label>
              </div>
            </div>

            <button type="submit" className="btn-primary mt-6">
              Submit Allocation Application
            </button>
          </form>
        </div>
      </main>
        </>
      )}

      {activeTab === 'home' && <Home />}
      {activeTab === 'matrix' && <RoomMatrix />}
      {activeTab === 'guidelines' && <Guidelines />}
      {activeTab === 'admin' && <AdminDashboard />}

    </div>
  );
}

import AdminDashboard from './AdminDashboard';

function RoomMatrix() {
  const [allRooms, setAllRooms] = useState([]);
  
  useEffect(() => {
    fetch('http://localhost:8080/api/rooms')
      .then(res => res.json())
      .then(data => setAllRooms(data))
      .catch(err => console.error("Failed to fetch all rooms:", err));
  }, []);

  return (
    <div className="room-matrix-container" style={{ padding: '40px 20px', maxWidth: '1200px', margin: '0 auto' }}>
      <h2 style={{ textAlign: 'center', marginBottom: '40px', color: '#1f3c88' }}>Campus Hostel Room Matrix & Occupancy Status</h2>
      <div className="matrix-grid" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '24px' }}>
        {allRooms.map((r, idx) => {
          let badgeColor = '#f59e0b'; // amber
          let statusText = 'Partially Occupied';
          if (r.current_occupancy === 0) {
            badgeColor = '#10b981'; // green
            statusText = 'Fully Vacant';
          } else if (r.current_occupancy >= r.capacity) {
            badgeColor = '#ef4444'; // red
            statusText = 'Full';
          }
          return (
            <div key={idx} className="room-card" onClick={() => alert('Room Details (Assigned Students) would show here.')} style={{ cursor: 'pointer', border: '1px solid #e5e7eb', borderRadius: '12px', padding: '20px', backgroundColor: '#fff', boxShadow: '0 4px 6px rgba(0,0,0,0.05)', transition: 'transform 0.2s', '&:hover': { transform: 'translateY(-5px)' } }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '15px' }}>
                <h3 style={{ margin: 0, fontSize: '1.5rem', color: '#111827' }}>Room {r.room_no}</h3>
                <span style={{ backgroundColor: '#e0e7ff', color: '#3730a3', padding: '4px 10px', borderRadius: '12px', fontSize: '0.8rem', fontWeight: 'bold' }}>{r.type}</span>
              </div>
              <div style={{ display: 'inline-block', backgroundColor: badgeColor, color: 'white', padding: '4px 12px', borderRadius: '20px', fontSize: '0.85rem', fontWeight: 'bold', marginBottom: '15px' }}>
                {statusText}
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.9rem', color: '#4b5563', marginBottom: '5px' }}>
                <span>Occupancy</span>
                <span>{r.current_occupancy} / {r.capacity}</span>
              </div>
              <div style={{ width: '100%', backgroundColor: '#f3f4f6', height: '8px', borderRadius: '4px', overflow: 'hidden', marginBottom: '20px' }}>
                <div style={{ width: `${(r.current_occupancy / r.capacity) * 100}%`, backgroundColor: badgeColor, height: '100%', transition: 'width 0.5s ease' }}></div>
              </div>
              <div style={{ fontSize: '1.25rem', fontWeight: 'bold', color: '#1f3c88' }}>
                ₹{r.base_price.toLocaleString('en-IN')} <span style={{ fontSize: '0.85rem', color: '#6b7280', fontWeight: 'normal' }}>/ year</span>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

function Guidelines() {
  const cardStyle = {
    backgroundColor: '#fff',
    border: '1px solid #e5e7eb',
    borderRadius: '12px',
    padding: '24px',
    boxShadow: '0 4px 6px rgba(0,0,0,0.05)',
    transition: 'transform 0.2s, box-shadow 0.2s',
    cursor: 'default'
  };
  
  const titleStyle = {
    color: '#A91D22', // Crimson
    fontSize: '1.25rem',
    marginBottom: '15px',
    borderBottom: '2px solid #D97706', // Gold
    paddingBottom: '10px'
  };
  
  return (
    <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '40px 20px', color: '#111827' }}>
      
      {/* Cards Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(400px, 1fr))', gap: '30px', marginBottom: '50px' }}>
        
        <div style={cardStyle} onMouseEnter={(e) => { e.currentTarget.style.transform = 'translateY(-5px)'; e.currentTarget.style.boxShadow = '0 10px 15px rgba(0,0,0,0.1)'; }} onMouseLeave={(e) => { e.currentTarget.style.transform = 'none'; e.currentTarget.style.boxShadow = '0 4px 6px rgba(0,0,0,0.05)'; }}>
          <h3 style={titleStyle}>Resident Code of Conduct & Curfew</h3>
          <ul style={{ paddingLeft: '20px', lineHeight: '1.6', margin: 0 }}>
            <li><strong>Gate curfew:</strong> 9:30 PM sharp on weekdays, 10:00 PM on weekends. Late entry requires prior digital warden pass.</li>
            <li>Biometric entry/exit logging is mandatory at the main security post.</li>
            <li><strong>Quiet hours:</strong> strictly observed between 10:30 PM and 6:00 AM.</li>
            <li>Zero tolerance policy for ragging, illicit substances, and unauthorized guests.</li>
          </ul>
        </div>
        
        <div style={cardStyle} onMouseEnter={(e) => { e.currentTarget.style.transform = 'translateY(-5px)'; e.currentTarget.style.boxShadow = '0 10px 15px rgba(0,0,0,0.1)'; }} onMouseLeave={(e) => { e.currentTarget.style.transform = 'none'; e.currentTarget.style.boxShadow = '0 4px 6px rgba(0,0,0,0.05)'; }}>
          <h3 style={titleStyle}>Room Allocation & Inventory Regulations</h3>
          <ul style={{ paddingLeft: '20px', lineHeight: '1.6', margin: 0 }}>
            <li>Rooms are allotted for one academic year. Sub-letting or informal swapping is strictly forbidden.</li>
            <li>Each resident is provided a study table, ergonomic chair, wardrobe, and cot with mattress. Fixture condition forms must be signed at check-in.</li>
            <li>Room change requests accepted only within the first 14 days of the semester via the administration portal.</li>
          </ul>
        </div>

        <div style={cardStyle} onMouseEnter={(e) => { e.currentTarget.style.transform = 'translateY(-5px)'; e.currentTarget.style.boxShadow = '0 10px 15px rgba(0,0,0,0.1)'; }} onMouseLeave={(e) => { e.currentTarget.style.transform = 'none'; e.currentTarget.style.boxShadow = '0 4px 6px rgba(0,0,0,0.05)'; }}>
          <h3 style={titleStyle}>Dining Hall & Mess Timings</h3>
          <ul style={{ paddingLeft: '20px', lineHeight: '1.6', margin: 0 }}>
            <li><strong>Breakfast:</strong> 07:30 AM – 09:30 AM</li>
            <li><strong>Lunch:</strong> 12:30 PM – 02:30 PM</li>
            <li><strong>Evening High Tea:</strong> 05:00 PM – 06:15 PM</li>
            <li><strong>Dinner:</strong> 07:30 PM – 09:30 PM</li>
            <li>Meal plan switching (Standard / Premium / Veg-only) must be submitted before the 25th of every month.</li>
          </ul>
        </div>

        <div style={cardStyle} onMouseEnter={(e) => { e.currentTarget.style.transform = 'translateY(-5px)'; e.currentTarget.style.boxShadow = '0 10px 15px rgba(0,0,0,0.1)'; }} onMouseLeave={(e) => { e.currentTarget.style.transform = 'none'; e.currentTarget.style.boxShadow = '0 4px 6px rgba(0,0,0,0.05)'; }}>
          <h3 style={titleStyle}>Fee Dues, Caution Deposit & Refund Policy</h3>
          <ul style={{ paddingLeft: '20px', lineHeight: '1.6', margin: 0 }}>
            <li>Semester hostel fees must be cleared within 10 days of allotment to prevent provisional cancellation.</li>
            <li>Security caution deposit refundable upon graduation subject to room inspection clearance.</li>
          </ul>
        </div>

      </div>

      {/* Official Documents */}
      <div style={{ marginBottom: '50px' }}>
        <h3 style={{ fontSize: '1.5rem', color: '#111827', marginBottom: '20px' }}>Official Documents</h3>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
          
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', backgroundColor: '#f9fafb', padding: '15px 25px', borderRadius: '8px', border: '1px solid #e5e7eb', flexWrap: 'wrap', gap: '15px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
              <span style={{ backgroundColor: '#A91D22', color: 'white', padding: '4px 8px', borderRadius: '4px', fontSize: '0.8rem', fontWeight: 'bold' }}>PDF</span>
              <span style={{ fontWeight: '500' }}>DESPU Hostel Resident Handbook & Disciplinary Guidelines</span>
            </div>
            <button style={{ padding: '8px 16px', fontSize: '0.9rem', backgroundColor: '#D97706', border: 'none', color: '#fff', borderRadius: '6px', cursor: 'pointer', transition: 'background-color 0.2s' }} onMouseEnter={(e) => e.target.style.backgroundColor = '#b45309'} onMouseLeave={(e) => e.target.style.backgroundColor = '#D97706'}>Download / Print</button>
          </div>

          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', backgroundColor: '#f9fafb', padding: '15px 25px', borderRadius: '8px', border: '1px solid #e5e7eb', flexWrap: 'wrap', gap: '15px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
              <span style={{ backgroundColor: '#A91D22', color: 'white', padding: '4px 8px', borderRadius: '4px', fontSize: '0.8rem', fontWeight: 'bold' }}>PDF</span>
              <span style={{ fontWeight: '500' }}>Weekly Mess Nutritional Plan & Rotation Menu</span>
            </div>
            <button style={{ padding: '8px 16px', fontSize: '0.9rem', backgroundColor: '#D97706', border: 'none', color: '#fff', borderRadius: '6px', cursor: 'pointer', transition: 'background-color 0.2s' }} onMouseEnter={(e) => e.target.style.backgroundColor = '#b45309'} onMouseLeave={(e) => e.target.style.backgroundColor = '#D97706'}>Download</button>
          </div>

        </div>
      </div>

      {/* Emergency & Warden Matrix */}
      <div style={{ backgroundColor: '#111827', color: '#fff', borderRadius: '12px', padding: '30px', display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '20px' }}>
        <div>
          <h4 style={{ color: '#D97706', marginBottom: '10px', fontSize: '1.1rem', margin: '0 0 10px 0' }}>Chief Warden Office</h4>
          <p style={{ margin: '5px 0', fontSize: '0.9rem', color: '#d1d5db' }}>Ground Floor, Admin Block</p>
          <p style={{ margin: '5px 0', fontSize: '0.9rem', fontWeight: 'bold' }}>ext: 2101</p>
        </div>
        <div>
          <h4 style={{ color: '#D97706', marginBottom: '10px', fontSize: '1.1rem', margin: '0 0 10px 0' }}>Boys' Hostel Warden</h4>
          <p style={{ margin: '5px 0', fontSize: '0.9rem', color: '#d1d5db' }}>Block B Office</p>
          <p style={{ margin: '5px 0', fontSize: '0.9rem', fontWeight: 'bold' }}>+91 98220 00000</p>
        </div>
        <div>
          <h4 style={{ color: '#D97706', marginBottom: '10px', fontSize: '1.1rem', margin: '0 0 10px 0' }}>24/7 Campus Security Control</h4>
          <p style={{ margin: '5px 0', fontSize: '0.9rem', color: '#d1d5db' }}>Main Gate Post</p>
          <p style={{ margin: '5px 0', fontSize: '0.9rem', fontWeight: 'bold' }}>+91 98221 11111</p>
        </div>
        <div>
          <h4 style={{ color: '#D97706', marginBottom: '10px', fontSize: '1.1rem', margin: '0 0 10px 0' }}>Campus Health & Medical Center</h4>
          <p style={{ margin: '5px 0', fontSize: '0.9rem', color: '#d1d5db' }}>Health Centre Wing</p>
          <p style={{ margin: '5px 0', fontSize: '0.9rem', fontWeight: 'bold' }}>ext: 108</p>
        </div>
      </div>

    </div>
  );
}

function Home() {
  const cardStyle = {
    backgroundColor: '#fff',
    border: '1px solid #e5e7eb',
    borderRadius: '12px',
    padding: '24px',
    boxShadow: '0 4px 6px rgba(0,0,0,0.05)',
    transition: 'transform 0.2s, box-shadow 0.2s',
    cursor: 'default'
  };

  const statCardStyle = {
    backgroundColor: '#111827',
    color: '#fff',
    borderRadius: '12px',
    padding: '24px',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    textAlign: 'center',
    boxShadow: '0 4px 6px rgba(0,0,0,0.1)'
  };

  return (
    <div style={{ padding: '0' }}>
      <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '40px 20px' }}>
        
        {/* Live Stats Bar */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '20px', marginBottom: '60px' }}>
          <div style={statCardStyle}>
            <span style={{ fontSize: '2.5rem', marginBottom: '15px' }}>🛏️</span>
            <h3 style={{ margin: '0 0 5px 0', fontSize: '1.5rem', color: '#D97706' }}>120+</h3>
            <p style={{ margin: 0, fontSize: '0.95rem', color: '#d1d5db' }}>Total Rooms Across Blocks</p>
          </div>
          <div style={statCardStyle}>
            <span style={{ fontSize: '2.5rem', marginBottom: '15px' }}>👥</span>
            <h3 style={{ margin: '0 0 5px 0', fontSize: '1.5rem', color: '#D97706' }}>350+</h3>
            <p style={{ margin: 0, fontSize: '0.95rem', color: '#d1d5db' }}>Active Residents</p>
          </div>
          <div style={statCardStyle}>
            <span style={{ fontSize: '2.5rem', marginBottom: '15px' }}>🍽️</span>
            <h3 style={{ margin: '0 0 5px 0', fontSize: '1.5rem', color: '#D97706' }}>1,000+</h3>
            <p style={{ margin: 0, fontSize: '0.95rem', color: '#d1d5db' }}>Daily Meals Served</p>
          </div>
          <div style={statCardStyle}>
            <span style={{ fontSize: '2.5rem', marginBottom: '15px' }}>🛡️</span>
            <h3 style={{ margin: '0 0 5px 0', fontSize: '1.5rem', color: '#D97706' }}>24/7</h3>
            <p style={{ margin: 0, fontSize: '0.95rem', color: '#d1d5db' }}>Campus Security Monitored</p>
          </div>
        </div>

        {/* Residential Blocks & Facilities */}
        <h2 style={{ textAlign: 'center', marginBottom: '40px', color: '#111827', fontSize: '2rem' }}>Campus Facilities & Living Quarters</h2>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '30px', marginBottom: '60px' }}>
          <div style={cardStyle} onMouseEnter={(e) => { e.currentTarget.style.transform = 'translateY(-5px)'; e.currentTarget.style.boxShadow = '0 10px 15px rgba(0,0,0,0.1)'; }} onMouseLeave={(e) => { e.currentTarget.style.transform = 'none'; e.currentTarget.style.boxShadow = '0 4px 6px rgba(0,0,0,0.05)'; }}>
            <h3 style={{ color: '#A91D22', fontSize: '1.3rem', marginBottom: '15px' }}>Block A - Junior Residents</h3>
            <p style={{ margin: 0, color: '#4b5563', lineHeight: '1.6' }}>Shared occupancy rooms with centralized study halls, high-speed Wi-Fi, and peer mentorship zones.</p>
          </div>
          <div style={cardStyle} onMouseEnter={(e) => { e.currentTarget.style.transform = 'translateY(-5px)'; e.currentTarget.style.boxShadow = '0 10px 15px rgba(0,0,0,0.1)'; }} onMouseLeave={(e) => { e.currentTarget.style.transform = 'none'; e.currentTarget.style.boxShadow = '0 4px 6px rgba(0,0,0,0.05)'; }}>
            <h3 style={{ color: '#A91D22', fontSize: '1.3rem', marginBottom: '15px' }}>Block B - Senior Residency</h3>
            <p style={{ margin: 0, color: '#4b5563', lineHeight: '1.6' }}>Single and premium rooms featuring ergonomic desks, dedicated quiet zones, and solar water heating.</p>
          </div>
          <div style={cardStyle} onMouseEnter={(e) => { e.currentTarget.style.transform = 'translateY(-5px)'; e.currentTarget.style.boxShadow = '0 10px 15px rgba(0,0,0,0.1)'; }} onMouseLeave={(e) => { e.currentTarget.style.transform = 'none'; e.currentTarget.style.boxShadow = '0 4px 6px rgba(0,0,0,0.05)'; }}>
            <h3 style={{ color: '#A91D22', fontSize: '1.3rem', marginBottom: '15px' }}>Central Dining Hall</h3>
            <p style={{ margin: 0, color: '#4b5563', lineHeight: '1.6' }}>Hygienic standard, veg-special, and multi-cuisine meal plans served four times daily in an open, sanitized environment.</p>
          </div>
          <div style={cardStyle} onMouseEnter={(e) => { e.currentTarget.style.transform = 'translateY(-5px)'; e.currentTarget.style.boxShadow = '0 10px 15px rgba(0,0,0,0.1)'; }} onMouseLeave={(e) => { e.currentTarget.style.transform = 'none'; e.currentTarget.style.boxShadow = '0 4px 6px rgba(0,0,0,0.05)'; }}>
            <h3 style={{ color: '#A91D22', fontSize: '1.3rem', marginBottom: '15px' }}>Campus Amenities</h3>
            <p style={{ margin: 0, color: '#4b5563', lineHeight: '1.6' }}>Indoor recreation room, modern laundry zones, fitness area, and a 24/7 emergency medical center.</p>
          </div>
        </div>

        {/* Institutional Notice Bulletin */}
        <div style={{ backgroundColor: '#f9fafb', borderRadius: '12px', border: '1px solid #e5e7eb', padding: '35px' }}>
          <h3 style={{ color: '#111827', fontSize: '1.5rem', marginBottom: '25px', display: 'flex', alignItems: 'center', gap: '10px' }}>
            <span>📢</span> Campus Housing Announcements
          </h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid #e5e7eb', paddingBottom: '20px', flexWrap: 'wrap', gap: '10px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
                <span style={{ backgroundColor: '#A91D22', color: '#fff', padding: '4px 10px', borderRadius: '6px', fontSize: '0.8rem', fontWeight: 'bold' }}>URGENT</span>
                <span style={{ fontWeight: '600', color: '#1f2937', fontSize: '1.05rem' }}>Admissions Open for Winter Semester</span>
              </div>
              <span style={{ color: '#6b7280', fontSize: '0.9rem' }}>Oct 12, 2026</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid #e5e7eb', paddingBottom: '20px', flexWrap: 'wrap', gap: '10px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
                <span style={{ backgroundColor: '#10b981', color: '#fff', padding: '4px 10px', borderRadius: '6px', fontSize: '0.8rem', fontWeight: 'bold' }}>INFO</span>
                <span style={{ fontWeight: '600', color: '#1f2937', fontSize: '1.05rem' }}>Updated Mess Menu Rotation Uploaded</span>
              </div>
              <span style={{ color: '#6b7280', fontSize: '0.9rem' }}>Oct 10, 2026</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '10px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
                <span style={{ backgroundColor: '#D97706', color: '#fff', padding: '4px 10px', borderRadius: '6px', fontSize: '0.8rem', fontWeight: 'bold' }}>MAINTENANCE</span>
                <span style={{ fontWeight: '600', color: '#1f2937', fontSize: '1.05rem' }}>Block A Wi-Fi Routine Maintenance Schedule</span>
              </div>
              <span style={{ color: '#6b7280', fontSize: '0.9rem' }}>Oct 08, 2026</span>
            </div>
          </div>
        </div>

      </div>

      {/* Institutional Footer */}
      <footer style={{ backgroundColor: '#111827', color: '#d1d5db', padding: '50px 20px 30px 20px', marginTop: '60px' }}>
        <div style={{ maxWidth: '1200px', margin: '0 auto', display: 'flex', flexWrap: 'wrap', justifyContent: 'space-between', gap: '40px' }}>
          <div style={{ flex: '1 1 300px' }}>
            <h4 style={{ color: '#fff', fontSize: '1.3rem', marginBottom: '15px' }}>Deccan Education Society</h4>
            <p style={{ margin: '0 0 10px 0', color: '#D97706', fontWeight: 'bold' }}>Established 1884</p>
            <p style={{ margin: 0, fontSize: '0.95rem', lineHeight: '1.7', color: '#9ca3af' }}>DES Pune University Campus,<br/>Shivajinagar, Pune, Maharashtra 411004</p>
          </div>
          <div style={{ flex: '1 1 200px' }}>
            <h4 style={{ color: '#fff', fontSize: '1.15rem', marginBottom: '15px' }}>Quick Links</h4>
            <ul style={{ listStyle: 'none', padding: 0, margin: 0, fontSize: '0.95rem', lineHeight: '2' }}>
              <li><a href="#" style={{ color: '#9ca3af', textDecoration: 'none', transition: 'color 0.2s' }} onMouseEnter={(e) => e.target.style.color = '#fff'} onMouseLeave={(e) => e.target.style.color = '#9ca3af'}>University Main Portal</a></li>
              <li><a href="#" style={{ color: '#9ca3af', textDecoration: 'none', transition: 'color 0.2s' }} onMouseEnter={(e) => e.target.style.color = '#fff'} onMouseLeave={(e) => e.target.style.color = '#9ca3af'}>Library Services</a></li>
              <li><a href="#" style={{ color: '#9ca3af', textDecoration: 'none', transition: 'color 0.2s' }} onMouseEnter={(e) => e.target.style.color = '#fff'} onMouseLeave={(e) => e.target.style.color = '#9ca3af'}>Student Grievance Cell</a></li>
            </ul>
          </div>
          <div style={{ flex: '1 1 250px' }}>
            <h4 style={{ color: '#fff', fontSize: '1.15rem', marginBottom: '15px' }}>Contact Support</h4>
            <p style={{ margin: '0 0 8px 0', fontSize: '0.95rem', color: '#9ca3af' }}>IT Helpdesk: <strong style={{ color: '#fff' }}>it.support@despu.edu.in</strong></p>
            <p style={{ margin: '0 0 8px 0', fontSize: '0.95rem', color: '#9ca3af' }}>Admissions: <strong style={{ color: '#fff' }}>admissions@despu.edu.in</strong></p>
            <p style={{ margin: 0, fontSize: '0.95rem', color: '#9ca3af' }}>Phone: <strong style={{ color: '#fff' }}>+91 20 0000 0000</strong></p>
          </div>
        </div>
        <div style={{ maxWidth: '1200px', margin: '40px auto 0 auto', borderTop: '1px solid #374151', paddingTop: '25px', textAlign: 'center', fontSize: '0.85rem', color: '#6b7280' }}>
          &copy; {new Date().getFullYear()} DES Pune University. All rights reserved.
        </div>
      </footer>
    </div>
  );
}

export default App;
