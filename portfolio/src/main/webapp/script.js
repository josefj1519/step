// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
function displayHamburgerDropdown(){
    dropdownContainer = document.getElementById('dropdown-container');
    if(dropdownContainer.style.display === "block"){
        dropdownContainer.style.display = "none";
    } else {
        dropdownContainer.style.display = "block";
    }
}

function onLoad(){
    fetchBlobstoreUrlAndShowForm();
    getShowTitles(5);
    getUserImages();
}

function addRandomQuote() {
  const quotes =
      ['Her?', 'Everything changed when the fire nation attacked', 'I\'m the one who knocks!'];

  // Pick a random quote.
  const quote = quotes[Math.floor(Math.random() * quotes.length)];

  // Add it to the page.
  const quoteContainer = document.getElementById('quote-container');
  quoteContainer.innerText = quote;
}

async function getShowTitles(maxComments){
  const response = await fetch('/data?count='+maxComments);
  const shows = await response.json();
  const showsContainer = document.getElementById('shows-container');
  showsContainer.innerText = shows;
}
 async function deleteShow() {
  await fetch('/delete-data', {method: 'POST'});
  await getShowTitles(0);
}

async function fetchBlobstoreUrlAndShowForm(){
    const response = await fetch('/blobstore-upload-url');
    const imageForm = document.getElementById('image-form');
    imageForm.action = await response.text();
    imageForm.classList.remove('hidden');
}

async function getUserImages(){
    const response = await fetch('/images');
    const images = await response.json();
    const imagesContainer = document.getElementById('image-container');
    images.forEach(imageUrl=>{
      var imageNode = document.createElement('IMG');
      imageNode.setAttribute('src',imageUrl);
      imagesContainer.appendChild(imageNode);
    });
}
