{
	let albumList, selectedAlbum, selectedImage, personalMessage, commentSection,
		pageOrchestrator = new PageOrchestrator(); // main controller
	var current_images;

	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") == null) {
			window.location.href = "index.html";
		} else {
			pageOrchestrator.start(); // initialize the components
			pageOrchestrator.refresh();
		} // display initial content
	}, false);


	function PersonalMessage(_username, messagecontainer) {
		this.username = _username;
		this.show = function() {
			messagecontainer.textContent = this.username;
		}
	}


	function AlbumList(userAlbumsAlert, othersAlbumsAlert, userAlbums, userAlbumsBody, otherAlbums, otherAlbumsBody, createAlbumForm) {
		this.userAlbumsAlert = userAlbumsAlert;
		this.othersAlbumsAlert = othersAlbumsAlert;
		this.userAlbumsContainer = userAlbums;
		this.userAlbumsBody = userAlbumsBody;
		this.othersAlbumsContainer = otherAlbums;
		this.othersAlbumsBody = otherAlbumsBody;
		this.createAlbumForm = createAlbumForm;
		this.saveButton = document.getElementById("saveButton");
		var albums;

		this.reset = function() {
			this.userAlbumsContainer.style.visibility = "hidden";
			this.othersAlbumsContainer.style.visibility = "hidden";
			this.saveButton.disabled = true;
		}

		
		this.registerEvents = function(orchestrator) {
			
			//BOTTONE PER CREARE UN NUOVO ALBUM
			this.createAlbumForm.querySelector("input[type='submit']").addEventListener('click', (e) => {
				var form = e.target.closest("form");
				console.log("sono in createAlbum");
				if (form.checkValidity()) {
					var self = this;
					makeCall("POST", 'CreateAlbum', form,
						function(req) {
							if (req.readyState == 4) {
								var message = req.responseText;
								if (req.status == 200) {
									document.getElementById("createAlbumAlert").textContent = "";
									orchestrator.refresh();
								} else if (req.status == 403) {
									window.location.href = req.getResponseHeader("Location");
									window.sessionStorage.removeItem('username');
								}
								else {
									document.getElementById("createAlbumAlert").textContent = message;
								}
							}
						}
					);
				} else {
					form.reportValidity();
				}
			})

			//PER GESTIRE IL CAMBIAMENTO DI ORDINE DEGLI ALBUM
			this.userAlbumsBody.addEventListener('dragover', (e) => {
				e.preventDefault();
				console.log("dragover");
				const afterElement = getDragAfterElement(userAlbumsBody, e.clientY);
				const draggable = document.querySelector('.dragging');
				if (afterElement == null) {
					this.userAlbumsBody.appendChild(draggable);
				} else {
					this.userAlbumsBody.insertBefore(draggable, afterElement);
				}

			})

			this.saveButton.addEventListener('click', () => {
				this.saveNewOrder();
			})
		}

		//PER OTTENERE TUTTI GLI ALBUM
		this.getAlbumList = function() {
			var self = this;
			makeCall("GET", "GetHomePage", null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							albums = JSON.parse(req.responseText);
							if (albums.length == 0) {
								return;
							}
							self.update(albums);
						} else {
							self.userAlbumsAlert.textContent = message;
						}
					}
				}
			);
		}

		//CARICA LE LISTE DI ALBUM NELLA HOME
		this.update = function(albumList) {
			var self = this;
			this.userAlbumsBody.innerHTML = "";
			this.othersAlbumsBody.innerHTML = "";
			albumList.forEach(function(album) {
				var row = document.createElement("p");
				row.setAttribute('albumId', album.id);
				//QUANDO CLICCHI UN ALBUM AGGIUNGE I PARAMETRI CHE SERVONO PER IL FORM ADD IMAGE
				row.addEventListener("click", (e) => { 
					document.getElementById("addImageForm").querySelector("input[type='hidden']").value = e.target.getAttribute("albumId");
					selectedAlbum.show(e.target.getAttribute("albumId")); // the list must know the details container
					document.querySelector("#selectedAlbumContainer").scrollIntoView({
						behavior: 'smooth'
					});
				}, false);
				row.textContent = "Title: " + album.title + " | Date: " + album.creationDate;
                
                //SE IL CREATORE E' L'USER AGGIUNGO LA POSSIBILITA' DI DRAG AND DROP
				var user = document.getElementById("id_username").textContent;
				if (album.creator == user.slice(0, -2)) { 
					row.draggable = true;
					row.setAttribute('class', "draggable");

					row.addEventListener('dragstart', () => {
						console.log("drag start");
						row.classList.add('dragging');
					})

					row.addEventListener('dragend', () => {
						row.classList.remove('dragging');
						self.oldOrder = self.order;
						self.order = self.getAlbumsOrder();
						console.log("new order: " + self.order);
						console.log("old order: " + self.oldOrder);
						if (self.order != self.oldOrder)
							self.saveButton.disabled = false;
					})
					self.userAlbumsBody.appendChild(row);
				} else {//ALTRIMENTI NON LA METTO
					row.textContent += " | Creator: " + album.creator;
					row.setAttribute('class', "notdraggable");
					self.othersAlbumsBody.appendChild(row);
				}
				if (self.othersAlbumsBody == "") {
					self.othersAlbumsAlert.textContent = "No albums here.";
				} if (self.userAlbumsBody == "") {
					self.userAlbumsAlert.textContent = "No albums here.";
				}

			});
			self.order = self.getAlbumsOrder();
			self.userAlbumsContainer.style.visibility = "visible";
			self.othersAlbumsContainer.style.visibility = "visible";
		}


		function getDragAfterElement(container, y) {
			const draggableElements = [...container.querySelectorAll('.draggable:not(.dragging)')];
			return draggableElements.reduce((closest, child) => {
				const box = child.getBoundingClientRect();
				const offset = y - box.top - box.height / 2;

				if (offset < 0 && offset > closest.offset) {
					console.log(child);
					return { offset: offset, element: child }
				} else {
					console.log(closest);
					return closest;
				}
			}, { offset: Number.NEGATIVE_INFINITY }).element;
		}

		this.getAlbumsOrder = function() {
			return [...this.userAlbumsContainer.querySelectorAll(".container p")].map(a => a.getAttribute("albumId")).toString();

		}

		this.saveNewOrder = function() {
			var self = this;
			const form = document.createElement("form"); //CREO UN FORM
			form.setAttribute('action', "#");

			//AGGIUNGO COME PARAMETRO IL NUOVO ORDINE DA SALVARE
			const i = document.createElement("input");
			i.setAttribute("type", "text");
			i.setAttribute("name", "newOrder");
			i.value = self.order;
			form.appendChild(i);

			if (form.checkValidity()) {
				var self = this;
				makeCall("POST", 'SaveNewAlbumsOrder', form,
					function(req) {
						if (req.readyState == 4) {
							var message = req.responseText;
							if (req.status == 200) {
								saveButton.disabled = true;
							} else if (req.status == 403) {
								window.location.href = req.getResponseHeader("Location");
								window.sessionStorage.removeItem('username');
							}
							else {
								self.userAlbumsAlert.textContent = message;
							}
						}
					}
				);
			} else {
				form.reportValidity();
			}
		}

		this.autoclick = function(albumId) {
			if (albumId) selectedAlbum.show(albumId);
		}
	}



	function SelectedAlbum(alertContainer, selectedAlbumContainer, imagesContainer, imagesBody, addImageForm, addCommentForm, previousButton, nextButton) {
		this.selectedAlbumAlert = alertContainer;
		this.selectedAlbumContainer = selectedAlbumContainer;
		this.addImageForm = addImageForm;
		this.imagesContainer = imagesContainer;
		this.imagesBody = imagesBody;
		this.addCommentForm = addCommentForm;
		var page;
		this.previousButton = previousButton;
		this.nextButton = nextButton;
		var self = this;

		this.reset = function() {
			self.selectedAlbumContainer.style.visibility = "hidden";
			self.selectedAlbumAlert.textContent = "";
			self.previousButton.style.visibility = "hidden";
			self.nextButton.style.visibility = "hidden";
		}
		
		//AGGIUNGO IL LISTENER AL FORM PER INSERIRE UN'IMMAGINE
		this.registerEvents = function(orchestrator) {
			this.addImageForm.querySelector("input[type='submit']").addEventListener('click', (e) => {
				var form = e.target.closest("form");
				if (form.checkValidity()) {
					var self = this;
					makeCall("POST", 'AddImageToAlbum', form,
						function(req) {
							if (req.readyState == 4) {
								var message = req.responseText;
								if (req.status == 200) {
									document.getElementById("createImageAlert").textContent = "";
									orchestrator.refresh();
								}else {
									document.getElementById("createImageAlert").textContent = message;

								}
							}
						}
					);
				} else {
					form.reportValidity();
				}
			});
		}

		//CARICO L'ALBUM SELEZIONATO
		this.show = function(album) {
			var self = this;
			var albumId = album;
			makeCall("GET", "GetAlbumPage?idAlbum=" + albumId, null, req => {
				if (req.readyState == 4) {
					var message = req.responseText;
					if (req.status == 200) {
						var images = JSON.parse(req.responseText);
	                    if (images.length == 0) {
							self.selectedAlbumAlert.textContent = "No Images.";
						    self.selectedAlbumContainer.style.visibility = "visible";
							document.getElementById("imagesContainer").style.visibility = "hidden";
							return;
						}
						document.getElementById("imagesContainer").style.visibility = "visible";
						page = 0;
						self.showTable(images);
						
					} else {
						self.alert.textContent = message;
					}
				}

			})
		}

		//CHIAMATO DA SHOWTABLE, MOSTRA LE 5 IMMAGINI DELLA PAGINA CORRENTE E CONTROLLA PREVIOUS E NEXT
		this.update = function() {
			var self = this;
			this.imagesBody.innerHTML = "";
			this.selectedAlbumAlert.innerHTML = "";
			self.checkPreviousNext();
			row = document.createElement("tr");
			for (var i = page * 5; i < page * 5 + 5 && current_images.length > i; i++) {
				var image = current_images[i];
				linkcell = document.createElement("td");
				titlecell = document.createElement("td");
				titlecell.textContent = image.title;
				img = document.createElement("img");
				img.setAttribute('imageid', image.id);
				img.setAttribute('imageTitle', image.title);
				img.setAttribute('imagePath', image.path);
				img.setAttribute('imageDescription', image.descriptionText);
				img.setAttribute('imageDate', image.date);
				img.setAttribute('albumId', image.idAlbum);
				img.setAttribute('id', 'imagechild');
				img.src = image.path;
				img.width = "150";
				linkcell.textContent =image.title + "\n";
				linkcell.appendChild(img);
				
				//LISTENER PER APRIRE LA FINESTRA MODALE QUANDO IL MOUSE PASSA SOPRA UN'IMMAGINE
				img.addEventListener("mouseenter", (e) => {
					addCommentForm.querySelector("input[type='hidden']").value = e.target.getAttribute("imageid");
					addCommentForm.querySelector("input[id='imageSelectedAlbumId']").value = e.target.getAttribute("albumId");
					selectedImage.openModal(this, e.target.getAttribute("imageid"),
						e.target.getAttribute("imageTitle"),
						e.target.getAttribute("imageDate"),
						e.target.getAttribute("imageDescription"),
						e.target.getAttribute("imagePath"));
				}, false);
				document.getElementById("closeButton").addEventListener('click', () => {
					selectedImage.closeModal();
				}, false);
				//anchor.href = "#";
				row.appendChild(linkcell);
			}

			this.imagesBody.appendChild(row);
			this.selectedAlbumContainer.style.visibility = "visible";
			document.querySelector("#selectedAlbumContainer").scrollIntoView({
				behavior: 'smooth'
			});
		};

		//CHIAMATO DA SHOW, DOPO AVER RICEVUTO LE IMMAGINI MOSTRA LA TABELLA
		this.showTable = function(images) {
			current_images = images;
			this.update();
		}

		this.checkPreviousNext = function() {
			if (page > 0) { //CONTROLLA SE IL BOTTONE PREVIOUS SIA DA ABILITARE
				this.previousButton.style.visibility = "visible";
				this.previousButton.addEventListener("click", this.previousPage, false);
			} else {
				this.previousButton.style.visibility = "hidden";
				this.previousButton.removeEventListener("click", this.previousPage, false);
			}
			//CONTROLLA SE IL BOTTONE NEXT SIA DA ABILITARE
			if (page < (current_images.length % 5 == 0 ? (current_images.length / 5) : (current_images.length / 5 - 1))) {
				this.nextButton.style.visibility = "visible";
				this.nextButton.addEventListener("click", this.nextPage, false);
			} else {
				this.nextButton.style.visibility = "hidden";
				this.nextButton.removeEventListener("click", this.nextPage, false);
			}
		}

		//CHIAMATI DAI LISTENER SUI BOTTONI
		this.previousPage = function(e) {
			page -= 1;
			self.update();
			selectedImage.closeModal();
		}

		this.nextPage = function(e) {
			page += 1;
			self.update();
			selectedImage.closeModal();
			
		}

	};


	function SelectedImage(alertContainer, selectedImageContainer, selectedImageBody) {
		this.alertContainer = alertContainer;
		this.selectedImageContainer = selectedImageContainer;
		this.selectedImageBody = selectedImageBody;
		var self = this;

		this.reset = function() {
			selectedImageContainer.style.visibility = "hidden";
		}
		
		//CARICA I DATI RELATIVI ALL'IMMAGINE SELEZIONATA
		this.update = function(imageid, imagetitle, imagedate, imagedescription, imagepath) {
			var row, imagerow, imagecell, descriptioncell;
			var self = this;
			this.selectedImageBody.innerHTML = "";
			row = document.createElement("tr");
			descriptioncell = document.createElement("td");
			descriptioncell.textContent = "Title: " + imagetitle + " Date: " + imagedate + " Description: " + imagedescription;
			row.appendChild(descriptioncell);
			imagerow = document.createElement("tr");
			imagecell = document.createElement("td");
			var img = new Image();
			img.src = imagepath;
			img.style.height = '300px';
			imagecell.appendChild(img);
			imagerow.appendChild(imagecell);
			self.selectedImageBody.appendChild(row);
			self.selectedImageBody.appendChild(imagerow);
			self.selectedImageContainer.style.visibility = "visible";
			commentSection.show(imageid);
		};

		//CHIAMATA DAL LISTENER IN SELECTED ALBUM
		this.openModal = function(anchor, imageid, imagetitle, imagedate, imagedescription, imagepath) {
			this.anchor = anchor;
			self.update(imageid, imagetitle, imagedate, imagedescription, imagepath);
		}

		this.closeModal = function() {
			if (selectedImageContainer == null) return;
			selectedImageContainer.style.visibility = "hidden";
			commentSection.reset();
		}

	};

	function CommentSection(alertContainer, selectedImageComments, commentsBody, addCommentForm) {
		this.alert = alertContainer;
		this.selectedImageComments = selectedImageComments;
		this.commentsBody = commentsBody;
		this.addCommentForm = addCommentForm;

		this.reset = function() {
			this.selectedImageComments.style.visibility = "hidden";
			this.addCommentForm.style.visibility = "hidden";
			this.commentsBody.style.visibility = "hidden";
		}

		//RICHIEDE I COMMENTI RELATIVI ALL'IMMAGINE SELEZIONATA
		this.show = function(imageId) {
			var self = this;
			this.addCommentForm.style.visibility = "visible";
			makeCall("GET", "GetImageComments?idImage=" + imageId, null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var comments = JSON.parse(req.responseText);
							if (comments.length == 0) {
								self.commentsBody.innerHTML = "";
								self.alert.textContent = "No Comments.";
								return;
							}
							self.update(comments);
						} else {
							self.alert.textContent = message;
						}
					}
				}
			);
		}
		
		//MOSTRA I COMMENTI RELATIVI ALL'IMMAGINE
		this.update = function(comments) {
			var self = this;
			var row, usercell, commentcell;		
			self.commentsBody.innerHTML = "";
			self.alert.textContent = "";
			comments.forEach(function(comment) {
				row = document.createElement("tr");
				usercell = document.createElement("td");
				usercell.textContent = comment.creatorUsername + ": ";
				commentcell = document.createElement("td");
				commentcell.textContent = comment.text;
				row.appendChild(usercell);
				row.appendChild(commentcell);
				self.commentsBody.appendChild(row);
			});
			self.commentsBody.style.visibility = "visible";
			self.addCommentForm.style.visibility = "visible";
		}

		//LISTENER PER AGGIUNGERE UN COMMENTO
		this.registerEvents = function(orchestrator) {
			this.addCommentForm.querySelector("input[type='submit']").addEventListener('click', (e) => {
				var form = e.target.closest("form");
				if (form.checkValidity()) {
					var self = this;
					album = document.getElementById("imageSelectedAlbumId").value;
					if(document.getElementById("commentText").value.length >0){
						makeCall("POST", 'AddComment', form,
							function(req) {
								if (req.readyState == 4) {
									var message = req.responseText;
									if (req.status == 200) {
										orchestrator.refresh(album);
									} else if (req.status == 403) {
										window.location.href = req.getResponseHeader("Location");
										window.sessionStorage.removeItem('username');
									}
									else {
										self.alert.textContent = message;
									}
								}
							}
						);
					}else{
						self.alert.textContent = "comment can't be empty";
					}

				} else {
					form.reportValidity();
				}
			});
		}

	}


	function PageOrchestrator() {

		this.start = function() {
			personalMessage = new PersonalMessage(sessionStorage.getItem('username'),
				document.getElementById("id_username"));
			personalMessage.show();

			albumList = new AlbumList(
				document.getElementById("userAlbumsAlert"),
				document.getElementById("othersAlbumsAlert"),
				document.getElementById("userAlbumsContainer"),
				document.getElementById("userAlbumsBody"),
				document.getElementById("othersAlbumsContainer"),
				document.getElementById("othersAlbumsBody"),
				document.getElementById("createAlbumForm"));
			albumList.registerEvents(this);

			selectedAlbum = new SelectedAlbum(
				document.getElementById("selectedAlbumAlert"),
				document.getElementById("selectedAlbumContainer"),
				document.getElementById("imagesContainer"),
				document.getElementById("imagesBody"),
				document.getElementById("addImageForm"),
				document.getElementById("addCommentForm"),
				document.getElementById("previousButton"),
				document.getElementById("nextButton"));

			selectedAlbum.registerEvents(this);

			selectedImage = new SelectedImage(
				document.getElementById("selectedImageAlert"),
				document.getElementById("selectedImageContainer"),
				document.getElementById("selectedImageBody"));

			commentSection = new CommentSection(
				document.getElementById("commentsAlert"),
				document.getElementById("selectedImageComments"),
				document.getElementById("commentsBody"),
				document.getElementById("addCommentForm"));
			commentSection.registerEvents(this);
		}
		
		this.refresh = function(currentAlbum) {
			albumList.reset();
			selectedAlbum.reset();
			selectedImage.reset();
			commentSection.reset();
			albumList.getAlbumList();
			albumList.autoclick(currentAlbum);
		}
	}
}