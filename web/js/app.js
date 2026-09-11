/* ============================================================
   MCA Resume Project: AI Job Portal Frontend JavaScript (ES6)
   ============================================================ */

let currentRole = 'student';
let allJobs = [];
let selectedJobForMatch = null;
let currentAiAnalysis = null;

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
  loadJobs();
  loadApplications();
  loadStats();
});

// Role Switcher
function switchRole(role) {
  currentRole = role;
  document.querySelectorAll('.role-btn').forEach(btn => btn.classList.remove('active'));
  document.querySelectorAll('.panel-pane').forEach(pane => pane.classList.remove('active'));

  document.getElementById(`btn-role-${role}`).classList.add('active');
  document.getElementById(`pane-${role}`).classList.add('active');

  if (role === 'recruiter') {
    loadApplications();
  } else if (role === 'admin') {
    loadStats();
    renderAdminJobs();
  }
}

// Fetch Jobs
async function loadJobs() {
  try {
    const res = await fetch('/api/jobs');
    const data = await res.json();
    if (data.ok) {
      allJobs = data.jobs;
      renderJobGrid(allJobs);
      renderAdminJobs();
      document.getElementById('stat-active-jobs').innerText = allJobs.length;
      document.getElementById('rec-total-jobs').innerText = allJobs.length;
      document.getElementById('admin-stat-jobs').innerText = allJobs.length;
    }
  } catch (err) {
    console.error('Failed to load jobs:', err);
  }
}

// Render Job Grid for Student
function renderJobGrid(jobs) {
  const grid = document.getElementById('student-job-grid');
  if (!jobs || jobs.length === 0) {
    grid.innerHTML = `<div style="grid-column: 1/-1; text-align: center; padding: 40px; color: var(--text-muted);">No job listings match your filter criteria.</div>`;
    return;
  }

  grid.innerHTML = jobs.map(job => `
    <div class="job-card">
      <div>
        <div class="job-card-header">
          <img src="${job.companyLogo || 'https://api.dicebear.com/7.x/identicon/svg?seed=' + encodeURIComponent(job.company)}" class="company-logo" alt="${job.company}" />
          <div class="job-info">
            <h3>${escapeHtml(job.title)}</h3>
            <div class="company">${escapeHtml(job.company)} • ${escapeHtml(job.location)}</div>
          </div>
        </div>

        <div class="job-tags">
          <span class="tag">${escapeHtml(job.roleCategory)}</span>
          <span class="tag">${escapeHtml(job.jobType)}</span>
          <span class="tag">${escapeHtml(job.experienceLevel)}</span>
        </div>

        <p class="job-desc">${escapeHtml(job.description)}</p>

        <div class="job-tags">
          ${(job.skills || []).map(s => `<span class="tag tag-skill">${escapeHtml(s)}</span>`).join('')}
        </div>
      </div>

      <div>
        <div class="job-meta">
          <span>💰 ${escapeHtml(job.salaryRange)}</span>
          <span>👥 ${job.applicantCount || 0} Applied</span>
        </div>

        <div class="job-actions">
          <button class="btn btn-ai" onclick="openAiMatchModal('${job.id}')">🤖 AI Match Resume</button>
          <button class="btn btn-primary" onclick="openAiMatchModal('${job.id}')">Apply Now</button>
        </div>
      </div>
    </div>
  `).join('');
}

// Filter Jobs
function filterStudentJobs() {
  const query = document.getElementById('student-search').value.toLowerCase();
  const role = document.getElementById('filter-role').value;
  const type = document.getElementById('filter-type').value;

  const filtered = allJobs.filter(job => {
    const matchQuery = !query || job.title.toLowerCase().includes(query) ||
                       job.company.toLowerCase().includes(query) ||
                       job.description.toLowerCase().includes(query) ||
                       job.skills.some(s => s.toLowerCase().includes(query));

    const matchRole = role === 'All' || job.roleCategory === role;
    const matchType = type === 'All' || job.jobType === type;

    return matchQuery && matchRole && matchType;
  });

  renderJobGrid(filtered);
}

function resetFilters() {
  document.getElementById('student-search').value = '';
  document.getElementById('filter-role').value = 'All';
  document.getElementById('filter-type').value = 'All';
  renderJobGrid(allJobs);
}

// AI Matcher Modal Logic
function openAiMatchModal(jobId) {
  selectedJobForMatch = allJobs.find(j => j.id === jobId);
  if (!selectedJobForMatch) return;

  document.getElementById('ai-modal-job-title').innerText = `AI Matcher: ${selectedJobForMatch.title}`;
  document.getElementById('ai-modal-company').innerText = `${selectedJobForMatch.company} • ${selectedJobForMatch.salaryRange}`;

  document.getElementById('ai-match-step-input').style.display = 'block';
  document.getElementById('ai-match-step-results').style.display = 'none';

  openModal('modal-ai-match');
}

function fillSampleMcaResume() {
  const sample = `OBJECTIVE:
Master of Computer Applications (MCA) graduate seeking a Full Stack Software Developer / Java Backend Engineer position.

TECHNICAL SKILLS:
- Languages: Java, Core Java, JavaScript, SQL, TypeScript
- Frameworks & Libraries: Spring Boot, Spring MVC, Express, React, HTML5, CSS3
- Databases & Tools: MySQL, PostgreSQL, REST APIs, Git, GitHub, Docker

PROJECT HIGHLIGHTS:
- MCA AI Job Portal & Resume Matcher: Developed an end-to-end Job Portal utilizing Java 21 backend, MySQL database, and HTML/CSS/JS frontend. Implemented custom AI resume analysis algorithms calculating ATS scores and missing skill keywords.
- E-Commerce REST API Backend: Engineered secure RESTful microservices with Spring Boot and Spring Data JPA.

EDUCATION:
- Master of Computer Applications (MCA) — 8.6 CGPA
- Bachelor of Computer Applications (BCA) — 8.2 CGPA`;

  document.getElementById('ai-resume-input').value = sample;
}

async function runAiAnalysis() {
  const text = document.getElementById('ai-resume-input').value.trim();
  if (!text || text.length < 20) {
    alert('Please enter or paste your resume text (at least 20 characters).');
    return;
  }

  try {
    const res = await fetch(`/api/jobs/${selectedJobForMatch.id}/match`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ resumeText: text })
    });

    const data = await res.json();
    if (data.ok) {
      currentAiAnalysis = data.analysis;
      renderAiResults(data.analysis);
    } else {
      alert(data.message || 'Analysis failed.');
    }
  } catch (err) {
    console.error('AI match error:', err);
    alert('Failed to connect to Java AI Server.');
  }
}

function renderAiResults(analysis) {
  document.getElementById('ai-match-step-input').style.display = 'none';
  document.getElementById('ai-match-step-results').style.display = 'block';

  document.getElementById('ai-score-val').innerText = `${analysis.matchPercentage}%`;
  document.getElementById('ai-score-grade').innerText = `GRADE ${analysis.matchGrade}`;
  document.getElementById('ai-score-circle').style.setProperty('--score', analysis.matchPercentage);

  document.getElementById('ai-summary-title').innerText = `${analysis.matchGrade} Grade Fit — ${analysis.matchPercentage}% Match`;
  document.getElementById('ai-summary-text').innerText = analysis.summary;

  document.getElementById('ai-ats-badge').innerText = `ATS Score: ${analysis.atsScore} (${analysis.atsGrade})`;

  document.getElementById('ai-matched-skills').innerHTML = (analysis.matchedSkills || [])
    .map(s => `<span class="tag" style="background:rgba(16,185,129,0.15); color:#34d399; border-color:rgba(16,185,129,0.3);">${escapeHtml(s)}</span>`).join('');

  document.getElementById('ai-missing-skills').innerHTML = (analysis.missingSkills || [])
    .map(s => `<span class="tag" style="background:rgba(239,68,68,0.15); color:#f87171; border-color:rgba(239,68,68,0.3);">${escapeHtml(s)}</span>`).join('');

  document.getElementById('ai-improvements-list').innerHTML = (analysis.improvements || [])
    .map(imp => `<li>${escapeHtml(imp)}</li>`).join('');
}

function backToAiInput() {
  document.getElementById('ai-match-step-results').style.display = 'none';
  document.getElementById('ai-match-step-input').style.display = 'block';
}

// Proceed to Submit Application
async function proceedToApply() {
  if (!selectedJobForMatch) return;

  const resumeText = document.getElementById('ai-resume-input').value;

  try {
    const res = await fetch('/api/applications', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        jobId: selectedJobForMatch.id,
        candidateName: 'Aarav Sharma (MCA Candidate)',
        candidateEmail: 'aarav.mca@gmail.com',
        candidatePhone: '+91 98765 12345',
        candidateRole: 'Full Stack Java Developer',
        resumeText: resumeText
      })
    });

    const data = await res.json();
    if (data.ok) {
      alert(`🎉 Application submitted successfully to ${selectedJobForMatch.company}! AI Match Score: ${currentAiAnalysis ? currentAiAnalysis.matchPercentage : 88}%`);
      closeModal('modal-ai-match');
      loadJobs();
      loadApplications();
    } else {
      alert(data.message || 'Application submission failed.');
    }
  } catch (err) {
    console.error('Submit application error:', err);
  }
}

// Applications Management
async function loadApplications() {
  try {
    const res = await fetch('/api/applications');
    const data = await res.json();
    if (data.ok) {
      renderStudentApps(data.applications);
      renderRecruiterApps(data.applications);
      document.getElementById('rec-total-apps').innerText = data.applications.length;
      document.getElementById('admin-stat-apps').innerText = data.applications.length;

      const shortlisted = data.applications.filter(a => a.status === 'Shortlisted' || a.status === 'Interview Scheduled' || a.status === 'Offered').length;
      document.getElementById('rec-shortlisted').innerText = shortlisted;
    }
  } catch (err) {
    console.error('Failed to load applications:', err);
  }
}

function renderStudentApps(apps) {
  const tbody = document.getElementById('student-apps-table');
  if (!apps || apps.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5" style="text-align:center; color: var(--text-muted);">No applications submitted yet.</td></tr>`;
    return;
  }

  tbody.innerHTML = apps.map(app => `
    <tr>
      <td>
        <strong>${escapeHtml(app.jobTitle)}</strong>
        <div style="font-size:12px; color:var(--text-muted);">${escapeHtml(app.company)}</div>
      </td>
      <td>${escapeHtml(app.appliedAt)}</td>
      <td>
        <span style="font-weight:700; color:var(--accent-cyan);">${app.matchScore}%</span> (${app.matchGrade})
      </td>
      <td>${app.atsScore} / 100</td>
      <td><span class="badge-status ${getStatusClass(app.status)}">${escapeHtml(app.status)}</span></td>
    </tr>
  `).join('');
}

function renderRecruiterApps(apps) {
  const tbody = document.getElementById('recruiter-apps-table');
  if (!apps || apps.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; color: var(--text-muted);">No candidate applications received yet.</td></tr>`;
    return;
  }

  tbody.innerHTML = apps.map(app => `
    <tr>
      <td>
        <strong>${escapeHtml(app.candidateName)}</strong>
        <div style="font-size:12px; color:var(--text-muted);">${escapeHtml(app.candidateEmail)}</div>
      </td>
      <td>${escapeHtml(app.jobTitle)}</td>
      <td><strong style="color:var(--accent-emerald);">${app.matchScore}%</strong> Match</td>
      <td>${app.atsScore} ATS</td>
      <td>${escapeHtml(app.appliedAt)}</td>
      <td><span class="badge-status ${getStatusClass(app.status)}">${escapeHtml(app.status)}</span></td>
      <td>
        <select class="filter-select" style="padding: 4px 8px; font-size:12px;" onchange="updateAppStatus('${app.id}', this.value)">
          <option value="Under Review" ${app.status === 'Under Review' ? 'selected' : ''}>Under Review</option>
          <option value="Shortlisted" ${app.status === 'Shortlisted' ? 'selected' : ''}>Shortlist Candidate</option>
          <option value="Interview Scheduled" ${app.status === 'Interview Scheduled' ? 'selected' : ''}>Schedule Interview</option>
          <option value="Offered" ${app.status === 'Offered' ? 'selected' : ''}>Extend Offer</option>
          <option value="Rejected" ${app.status === 'Rejected' ? 'selected' : ''}>Reject</option>
        </select>
      </td>
    </tr>
  `).join('');
}

async function updateAppStatus(appId, newStatus) {
  try {
    const res = await fetch(`/api/applications/${appId}/status`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ status: newStatus })
    });
    const data = await res.json();
    if (data.ok) {
      loadApplications();
    }
  } catch (err) {
    console.error('Status update failed:', err);
  }
}

// Render Admin Jobs Table
function renderAdminJobs() {
  const tbody = document.getElementById('admin-jobs-table');
  if (!allJobs || allJobs.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" style="text-align:center;">No jobs registered.</td></tr>`;
    return;
  }

  tbody.innerHTML = allJobs.map(job => `
    <tr>
      <td><code>${job.id}</code></td>
      <td>
        <strong>${escapeHtml(job.title)}</strong>
        <div style="font-size:12px; color:var(--text-muted);">${escapeHtml(job.company)}</div>
      </td>
      <td>${escapeHtml(job.roleCategory)}</td>
      <td>${escapeHtml(job.postedDate)}</td>
      <td>${job.applicantCount || 0}</td>
      <td><span class="badge-status status-shortlisted">${job.status}</span></td>
      <td>
        <button class="btn btn-secondary" style="padding:4px 8px; font-size:11px;" onclick="deleteJobAdmin('${job.id}')">Delete</button>
      </td>
    </tr>
  `).join('');
}

async function deleteJobAdmin(jobId) {
  if (!confirm('Are you sure you want to delete this job posting?')) return;
  try {
    const res = await fetch(`/api/jobs/${jobId}`, { method: 'DELETE' });
    const data = await res.json();
    if (data.ok) {
      loadJobs();
    }
  } catch (err) {
    console.error('Delete job error:', err);
  }
}

// Post New Job
async function handlePostJob(event) {
  event.preventDefault();
  const title = document.getElementById('post-title').value;
  const company = document.getElementById('post-company').value;
  const category = document.getElementById('post-category').value;
  const type = document.getElementById('post-type').value;
  const salary = document.getElementById('post-salary').value;
  const skills = document.getElementById('post-skills').value;
  const desc = document.getElementById('post-desc').value;

  try {
    const res = await fetch('/api/jobs', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        title, company, roleCategory: category, jobType: type,
        salaryRange: salary, skills, description: desc
      })
    });

    const data = await res.json();
    if (data.ok) {
      alert('🚀 Job opening published successfully!');
      closeModal('modal-post-job');
      document.getElementById('form-post-job').reset();
      loadJobs();
    }
  } catch (err) {
    console.error('Post job error:', err);
  }
}

// Admin Stats
async function loadStats() {
  try {
    const res = await fetch('/api/stats');
    const data = await res.json();
    if (data.ok) {
      document.getElementById('admin-stat-ai').innerText = data.aiAnalysisCount || 42;
    }
  } catch (err) {
    console.error('Stats error:', err);
  }
}

// Helpers
function getStatusClass(status) {
  if (status === 'Shortlisted' || status === 'Offered' || status === 'Interview Scheduled') return 'status-shortlisted';
  if (status === 'Rejected') return 'status-rejected';
  return 'status-review';
}

function toggleStudentSubTab(tab) {
  if (tab === 'my-applications') {
    document.getElementById('student-jobs-view').style.display = 'none';
    document.getElementById('student-apps-view').style.display = 'block';
  } else {
    document.getElementById('student-apps-view').style.display = 'none';
    document.getElementById('student-jobs-view').style.display = 'block';
  }
}

function openModal(id) {
  document.getElementById(id).classList.add('active');
}

function closeModal(id) {
  document.getElementById(id).classList.remove('active');
}

function escapeHtml(str) {
  if (!str) return '';
  return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}
